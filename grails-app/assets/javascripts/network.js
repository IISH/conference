var networkId = 0;

$(document).ready(function () {
	networkId = parseInt($('input[name=id]').val());

	$('.loading').click(function (e) {
		var element = $(this);

		ajaxCall(this, 'network/participantsNotScheduled', {network_id: networkId}, function (data) {
			var participantsContainer = $('#not-in-session');

			if (data.participants.length === 0) {
				participantsContainer.text('-');
			}
			else {
				var clone = participantsContainer.find('li:eq(0)').clone(true);
				participantsContainer.html("");

				for (var i = 0; i < data.participants.length; i++) {
					var item = clone.clone(true);
					item.find('.participant').text(data.participants[i].participant);
					item.find('.participant').attr('href', data.participants[i].url);

					var papersContainer = item.find('.participants');
					var paperClone = papersContainer.find('li');
					for (var j = 0; j < data.participants[i].papers.length; j++) {
						var paperItem = paperClone.clone(true);

						paperItem.find('.paper-text').text(data.participants[i].papers[j].name + " (" + data.participants[i].papers[j].state + ")");
						paperItem.find('input[name=paper-id]').val(data.participants[i].papers[j].paperId);
						paperItem.find('input[name=paper-state-id]').val(data.participants[i].papers[j].paperStateId);

						papersContainer.append(paperItem);
					}
					paperClone.remove();

					participantsContainer.append(item);
				}
			}

			element.remove();
			participantsContainer.show();
		});
	});

	$('.add-session').click(function (e) {
		$('.errors').hide();
		$('.message').hide();

		var element = $(this);

		ajaxCall(this, 'network/addSession', {network_id: networkId, session_id: element.prev().val()}, function (data) {
			setParticipantDataForNetwork(data);
		});
	});

	$('.add-new-session').click(function (e) {
		$('.errors').hide();
		$('.message').hide();

		var element = $(this);
		var parentElement = element.parent();

		var code = parentElement.find('.session-code');
		var name = parentElement.find('.session-name');

		ajaxCall(this, 'network/addSession',
			{   network_id: networkId,
				session_code: code.val(),
				session_name: name.val()},
			function (data) {
				setParticipantDataForNetwork(data);
			}
		);

		code.val("");
		name.val("");
	});

	$(document).on("removed-item", '.remove-session', function (e) {
		$('.errors').hide();
		$('.message').hide();

		var element = $(e.target);
		var parentElement = element.parent();

		ajaxCall(this, 'network/removeSession',
			{   network_id: networkId,
				session_id: parentElement.find('.session-id').val()},
			function (data) {
				setParticipantDataForNetwork(data);
			}
		);
	});

	$('.session-state-select').change(function (e) {
		var element = $(this);
		ajaxCall(this, 'session/changeState', {session_id: element.parents("span").prev().val(), state_id: element.val()},
			function (data) {
				setParticipantDataForSession(data, element.parents("span.session").next());
			}
		);
	});

	$(".toggle-session-participants").click(function (e) {
		$("ul.session-participants").toggle();
	});
});