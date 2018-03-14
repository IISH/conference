package org.iisg.eca.utils

/**
 * Extracts names from methods called on an instance of this class
 */
class MethodNamesExtractor {
    Set<String> methodNames = new TreeSet<>()

    def methodMissing(String name, args) {
        methodNames.add(name)
    }
}