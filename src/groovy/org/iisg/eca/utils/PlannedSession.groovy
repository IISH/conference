package org.iisg.eca.utils

/**
 * Represents information about a planned session, purely for the API
 */
final class PlannedSession {
    long roomId
    String roomName
    String roomNumber

    long dayId
    Date day

    long timeId
    int indexNumber
    String period

    long sessionId
    String sessionCode
    String sessionName
    String sessionAbstract

    List<Network> networks
    List<Participant> participants

    class Network {
        long networkId
        String networkName
    }

    class Participant {
        long typeId
        String type
        String participantName
    }

    class ParticipantWithPaper extends Participant {
        long paperId
        String paperName
        String coAuthors
	    boolean hasDownload = false
	    String paperAbstract
        int sortOrder
    }
}
