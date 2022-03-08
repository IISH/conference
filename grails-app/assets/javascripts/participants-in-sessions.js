var setParticipantDataForSession = function (data, participantsContainer) {
	if (participantsContainer === undefined) {
		participantsContainer = $('.session-participants');
	}

	var item = participantsContainer.find('li.hidden');
	item = item.clone(true);

	participantsContainer.html("");
	setParticipants(data.participants, participantsContainer, item);
	participantsContainer.append(item);

	var equipmentContainer = $('#session-equipment');
	equipmentContainer.html("");
	for (var i = 0; i < data.equipment.length; i++) {
		equipmentContainer.append("<li>" + data.equipment[i][0] + " (" + data.equipment[i][1] + ")" + "</li>");
	}
};

var setParticipantDataForNetwork = function (data, networkSessionsContainer) {
	if (networkSessionsContainer === undefined) {
		networkSessionsContainer = $('#network-sessions');
	}

	var clone = networkSessionsContainer.find('li.hidden');
	clone = clone.clone(true);

	networkSessionsContainer.html("");
	for (var i = 0; i < data.sessions.length; i++) {
		var item = clone.clone(true);
		var link = item.find('.session a').attr("href").replace("*id*", data.sessions[i].id);

		item.find('.session-id').val(data.sessions[i].id);
		item.find('.session a').attr("href", link).text(data.sessions[i].name);

		var participantsContainer = item.find('.session-participants');
		if (data.sessions[i].participants.length > 0) {
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
};

var setParticipants = function (data, container, clone) {
	for (var i = 0; i < data.length; i++) {
		var item = clone.clone(true);

		if (item.find('.participant-value a').size() > 0) {
			var link = item.find('.participant-value a').attr("href").replace("*id*", data[i].id);
			item.find('.participant-value a').attr("href", link).text(data[i].participant);
		}
		else {
			item.find('.participant-value').text(data[i].participant);
		}

		item.find('.user-id').val(data[i].id);
		item.find('.participant-value a').attr("href", link).text(data[i].participant);
		item.find('.participant-state-value').text("(" + data[i].state + ")");

		if (data[i].paper === null) {
			item.find('.participant-paper-value').remove();
            item.find('.participant-paper-coauthor-value').remove();
		}
		else {
			var paperItem = item.find('.participant-paper-value');
			paperItem.find('.paper-text .v').text(data[i].paper.label);
			paperItem.find('input[name=paper-id]').val(data[i].paper.paperId);
			paperItem.find('input[name=paper-state-id]').val(data[i].paper.paperStateId);

            var paperCoAuthorItem = item.find('.participant-paper-coauthor-value');
            if (data[i].paper.coauthors == null) {
                paperCoAuthorItem.remove();
            }
            else {
                paperCoAuthorItem.find('.v').text(data[i].paper.coauthors);
            }
		}

        if (data[i].paperCoAuthoring === null) {
            item.find('.participant-paper-coauthoring-value').remove();
            item.find('.participant-paper-coauthoring-coauthor-value').remove();
        }
        else {
            var coauthoringPaperItem = item.find('.participant-paper-coauthoring-value');

            coauthoringPaperItem.find('.paper-text .v').text(data[i].paperCoAuthoring.label);
            coauthoringPaperItem.find('input[name=paper-id]').val(data[i].paperCoAuthoring.paperId);
            coauthoringPaperItem.find('input[name=paper-state-id]').val(data[i].paperCoAuthoring.paperStateId);

            var coauthoringPaperCoAuthorItem = item.find('.participant-paper-coauthoring-coauthor-value');
            if (data[i].paperCoAuthoring.coauthors == null) {
                coauthoringPaperCoAuthorItem.remove();
            }
            else {
                coauthoringPaperCoAuthorItem.find('.v').text(data[i].paperCoAuthoring.coauthors);
            }
        }

		setTypes(data[i].types, item);

		container.append(item);
		item.removeClass('hidden');
	}
};

var setTypes = function (data, item) {
	var typeContainer = item.find('ul');
	var typeClone = typeContainer.find('.participant-type-value');
	var typeItem;

	for (var j = 0; j < data.length; j++) {
		typeItem = typeClone.clone(true);
		typeItem.find('.type-id').val(data[j].id);
		typeItem.find('.participant-type-val').text(data[j].type);
		if (data[j].id === 9) {
            typeItem.find('.ui-icon').remove();
		}
		typeContainer.append(typeItem);
	}

	typeClone.remove();
};