var setParticipantDataForSession = function(data) {
    var participantsContainer = $('.session-participants');
    var item = participantsContainer.find('li.hidden');
    item = item.clone(true);
    
    participantsContainer.html("");
    setParticipants(data.participants, participantsContainer, item);
    participantsContainer.append(item);

    var equipmentContainer = $('#session-equipment');
    equipmentContainer.html("");
    for (var i=0; i<data.equipment.length; i++) {
        equipmentContainer.append("<li>" + data.equipment[i][0] + " (" + data.equipment[i][1] + ")" + "</li>");
    }
}

var setParticipantDataForNetwork = function(data) {
    var networkSessionsContainer = $('#network-sessions');
    var clone = networkSessionsContainer.find('li.hidden');
    clone = clone.clone(true);

    networkSessionsContainer.html("");
    for (var i=0; i<data.sessions.length; i++) {
        var item = clone.clone(true);
        item.find('.session-id').val(data.sessions[i].id);
        item.find('.session').text(data.sessions[i].name);

        var participantsContainer = item.find('.session-participants');
        if (data.sessions[i].participants.length > 1) {
            var participantClone = participantsContainer.find('> li');
            setParticipants(data.sessions[i].participants, participantsContainer, participantClone.clone(true));
            participantClone.remove();
        }
        else {
            participantsContainer.remove();
        }

        networkSessionsContainer.append(item);
        item.removeClass('hidden');
    }
    
    networkSessionsContainer.append(clone);
}

var setParticipants = function(data, container, clone) {
    for (var i=0; i<data.length; i++) {
        var item = clone.clone(true);
        item.find('.user-id').val(data[i].id);
        item.find('.participant-value').text(data[i].participant);
        item.find('.participant-state-value').text("("+data[i].state+")");

        if (data[i].paper.trim().length === 0) {
            item.find('.participant-paper-value').remove();
        }
        else {
            item.find('.participant-paper-value').text(data[i].paper);
        }

        setTypes(data[i].types, item);

        container.append(item);
        item.removeClass('hidden');
    }
}

var setTypes = function(data, item) {
    var typeContainer = item.find('ul');
    var typeClone = typeContainer.find('.participant-type-value');
    var typeItem;

    for (var j=0; j<data.length; j++) {
        typeItem = typeClone.clone(true);
        typeItem.find('.type-id').val(data[j].id);
        typeItem.find('.participant-type-val').text(data[j].type);
        typeContainer.append(typeItem);
    }

    typeClone.remove();
}