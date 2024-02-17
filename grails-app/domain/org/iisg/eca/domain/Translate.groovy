package org.iisg.eca.domain

/**
 * Domain class of table holding all translation texts
 */
class Translate extends EventDomain {
	String originalTextMD5
	String translation

	static mapping = {
		table 'translation'
		cache true
		version false

		id                  column: 'translation_id'
		originalTextMD5     column: 'original_text_md5'
		translation         column: 'translation',      type: 'text'
	}

	static constraints = {
		originalTextMD5 maxSize: 32
	}

	/**
	 * Returns a map with all translations for the current event
	 * @return A map with all translations
	 */
	static Map<String, String> getTranslationsMap() {
		Map<String, String> translations = new HashMap<String, String>()
		list().each {
			translations.put(it.originalTextMD5, it.translation)
		}

		return translations
	}

	@Override
	String toString() {
		"Translation for $originalTextMD5"
	}
}
