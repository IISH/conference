<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>${page.toString()}</title>
	</head>
	<body>
        <ol class="property-list">
            <li>
                <span id="roomName-label" class="property-label">
                    <g:message code="room.roomname.label" />
                </span>
                <span class="property-value" arial-labelledby="roomName-label">
                    <g:fieldValue bean="${room}" field="roomName" />
                </span>
            </li>
            <li>
                <span id="roomNumber-label" class="property-label">
                    <g:message code="room.roomnumber.label" />
                </span>
                <span class="property-value" arial-labelledby="roomNumber-label">
                    <g:fieldValue bean="${room}" field="roomNumber" />
                </span>
            </li>
            <li>
                <span id="noOfSeats-label" class="property-label">
                    <g:message code="room.noofseats.label" />
                </span>
                <span class="property-value" arial-labelledby="noOfSeats-label">
                    <g:fieldValue bean="${room}" field="noOfSeats" />
                </span>
            </li>
            <li>
                <span id="comment-label" class="property-label">
                    <g:message code="room.comment.label" />
                </span>
                <span class="property-value" arial-labelledby="comment-label">
                    <g:fieldValue bean="${room}" field="comment" />
                </span>
            </li>
        </ol>
    </body>
</html>