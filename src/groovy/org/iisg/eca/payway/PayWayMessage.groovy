package org.iisg.eca.payway

import org.iisg.eca.domain.Setting
import org.iisg.eca.utils.AlwaysAllowHTTPBuilder

import java.text.SimpleDateFormat

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST

/**
 * Represents messages from and to PayWay
 */
class PayWayMessage extends TreeMap<String, Object> {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

	PayWayMessage() { }

	PayWayMessage(Map<? extends String, ? extends Object> message) {
		putAll(message)
	}

	/**
	 * Adds or overwrites a value for a valid parameter
	 * @param parameter The parameter to add
	 * @param value The value to add
	 */
	@Override
	Object put(String parameter, Object value) {
		parameter = parameter.toUpperCase().trim()

		// If value is ok, put both the parameter and the value into the map
		if ((value != null) && !value.toString().trim().isEmpty()) {
			if (value instanceof Date) {
				Date date = (Date) value
				return super.put(parameter, DATE_FORMAT.format(date))
			}
			else {
				return super.put(parameter, value.toString().trim())
			}
		}
		else {
			return null
		}
	}

	/**
	 * Returns the value for the given property
	 * @param parameter The parameter to obtain the value of
	 * @param date Whether the value is a date and has to be parsed
	 * @return The corresponding value
	 */
	Object get(String parameter, boolean date = false) {
		parameter = parameter.toUpperCase().trim()

		if (this.containsKey(parameter)) {
			return (date) ? DATE_FORMAT.parse(super.get(parameter).toString()) : super.get(parameter)
		}
		else {
			return null
		}
	}

	/**
	 * Sends this message to PayWay
	 * @param apiName The name of the PayWay API to send the message to
	 * @return Returns a new PayWayMessage with the response, unless there is an error. In that case, null is returned
	 */
	PayWayMessage send(String apiName) {
		PayWayMessage returnMessage = null

		addProject()
		signTransaction()

		AlwaysAllowHTTPBuilder http = new AlwaysAllowHTTPBuilder(Setting.getSetting(Setting.PAYWAY_ADDRESS).value)
		http.request(POST, JSON) {
			uri.path = apiName
			body = this

			response.success = { resp, json ->
				PayWayMessage message = new PayWayMessage(json)

				// Only return the message if it is valid
				if (message.isValid()) {
					returnMessage = message
				}
			}
		}

		return returnMessage
	}

	/**
	 * Signs the transaction with a SHA-1 hash
	 * @param messageIn Whether this an incoming message for PayWay or not
	 */
	void signTransaction(boolean messageIn = true) {
		// Make sure the signature is removed
		remove("SHASIGN")

		// Obtain the pass phrase
		// TODO: moet hier event als parameter bij komen ???
		String passPhrase = Setting.getSetting(Setting.PAYWAY_PASSPHRASE_IN).value.toString()
		if (!messageIn) {
			passPhrase = Setting.getSetting(Setting.PAYWAY_PASSPHRASE_OUT).value.toString()
		}

		// Create the hash
		List<String> keyValues = collect { k, v -> "$k=$v" }
		// TODO: passPhrase is null ???
		log.error('aaa789 ' + passPhrase)
		String toBeHashed = keyValues.join(passPhrase) + passPhrase
		String hash = toBeHashed.encodeAsSHA1()

		// Sign the transaction
		put("SHASIGN", hash)
	}

	/**
	 * Make sure the transaction is valid by checking the SHA-1 hash
	 */
	boolean isValid() {
		if ((this.get('SUCCESS') == null) || this.get('SUCCESS').toString().equalsIgnoreCase('true')) {
			// Obtain the current signature
			String originalHash = remove("SHASIGN")

			// Sign the transaction
			signTransaction(false)

			// Compare the hashes
			String newHash = get("SHASIGN")

			return newHash.equalsIgnoreCase(originalHash)
		}

		return false
	}

	/**
	 * Adds the project to the message
	 */
	private void addProject() {
		this.put('project', Setting.getSetting(Setting.PAYWAY_PROJECT).value)
	}
}
