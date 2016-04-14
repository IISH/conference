package org.iisg.eca.domain

/**
 * Domain class of table holding all IP authentication rules
 */
class IPAuthentication {
    def static emailService

    String startIP
    String endIP
    boolean allowed
    String description

    static mapping = {
        table 'ip_authentication'
        cache true
        version false

        id          column: 'ip_authentication_id'
        startIP     column: 'start_ip'
        endIP       column: 'end_ip'
        allowed     column: 'allowed'
        description column: 'description'
    }

    static constraints = {
        startIP     blank: false,   maxSize: 15
        endIP       blank: false,   maxSize: 15
        description nullable: true, maxSize: 255
    }

    /**
     * Returns a byte representation of the IP address, easier for comparing IP addresses
     * @param ipAddress The IP address to convert
     * @return The IP address in byte representation
     */
    static byte[] getByteRepresentationOfIP(String ipAddress) {
        InetAddress ip = InetAddress.getByName(ipAddress)
        ip.getAddress()
    }

    /**
     * Compares two IP addresses
     * @param firstIP The first IP address
     * @param secondIP The second IP address
     * @return 0 if IP addresses are equal, -1 if firstIP is smaller than secondIP
     * and 1 if firstIP is larger than secondIP
     */
    static int compareIPs(String firstIP, String secondIP) {
        compareIPs(getByteRepresentationOfIP(firstIP), getByteRepresentationOfIP(secondIP))
    }

    /**
     * Compares two IP addresses
     * @param firstIP The first IP address
     * @param secondIP The second IP address
     * @return 0 if IP addresses are equal, -1 if firstIP is smaller than secondIP
     * and 1 if firstIP is larger than secondIP
     */
    static int compareIPs(byte[] firstIP, byte[] secondIP) {
        if (firstIP.length < secondIP.length) {
            return -1
        }

        if (firstIP.length > secondIP.length) {
            return 1
        }

        for (int i=0; i<firstIP.length; i++) {
            int bit1 = (int) (firstIP[i] & 0xFF)
            int bit2 = (int) (secondIP[i] & 0xFF)

            if (bit1 < bit2) {
                return -1
            }

            if (bit1 > bit2) {
                return 1
            }
        }

        return 0
    }

    /**
     * Is the IP address allowed, according to black lists and white lists in the database?
     * @param ipAddress The IP address to check
     * @return <code>true</code> if the IP address is allowed access
     */
    static boolean isIPAllowed(String ipAddress) {
        isIPAllowed(getByteRepresentationOfIP(ipAddress))
    }

    /**
     * Is the IP address allowed, according to black lists and white lists in the database?
     * @param ipAddress The IP address to check
     * @return <code>true</code> if the IP address is allowed access
     */
    static boolean isIPAllowed(byte[] ipAddress) {
        // Is IP authentication enabled?
        Setting ipAuth = Setting.getSetting(Setting.IP_AUTHENTICATION)
        if (ipAuth.value.equals('0')) {
            return true
        }

        // First check the block IP addresses
        for (IPAuthentication rule : findAllByAllowed(false, [cache: true])) {
            byte[] start = getByteRepresentationOfIP(rule.startIP)
            byte[] end = getByteRepresentationOfIP(rule.endIP)

            // If the IP falls within the range, IP is not allowed
            if ((compareIPs(ipAddress, start) >= 0) && (compareIPs(ipAddress, end) <= 0)) {
                return false
            }
        }

        // See if we should check allowed IP addresses
        Setting checkAllowed = Setting.getSetting(Setting.CHECK_ACCEPTED_IP)
        if (!checkAllowed.getBooleanValue()) {
            return true
        }

        // Check if the IP address is explicitly allowed
        for (IPAuthentication rule : findAllByAllowed(true, [cache: true])) {
            byte[] start = getByteRepresentationOfIP(rule.startIP)
            byte[] end = getByteRepresentationOfIP(rule.endIP)

            // If the IP falls within the range, IP is allowed
            if ((compareIPs(ipAddress, start) >= 0) && (compareIPs(ipAddress, end) <= 0)) {
                return true
            }
        }

        // In this stage, the IP is not in the white list, inform the super admin about this
        String ip = InetAddress.getByAddress(ipAddress).getHostAddress()
        emailService.sendInfoMail("IP ${ip} not accepted!", """\
            The conference application just denied access to a user with IP address: ${ip}
            Date/time: ${new Date()}
        """.stripIndent().toString())

        return false
    }
}
