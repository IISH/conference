var sessionId = 0;

$(document).ready(function () {
	sessionId = parseInt($('input[name=id]').val());

	$('#tabs').tabs({
		collapsible: true,
		active: false,
		activate: function (event, ui) {
			$('.select-participant').val("");
			$('.ui-tabs-panel .participant-id').val("");
			$('.ui-tabs-panel .paper-id').html("");
		}
	});

	$('.select-participant').each(function (e) {
		$(this).autocomplete({
			minLength: 3,
			source: function (request, response) {
				var typeId = $(this.element).parents("div.ui-tabs-panel").find('.type-id').val();

				$.getJSON(guessUrl('session/sessionParticipantsAutoComplete'), {terms: request.term, session_id: sessionId, type_id: typeId}, function (data) {
					response(data);
				});
			},
			search: function (event, ui) {
				$(event.target).parents('.ui-tabs-panel').find('.participant-id').val("");
			},
			focus: function (event, ui) {
				$(event.target).val(ui.item.label);
				return false;
			},
			select: function (event, ui) {
				var element = $(event.target);
				element.val(ui.item.label);
				element.parents('.ui-tabs-panel').find('.participant-id').val(ui.item.value);

				var paper = element.parents('.ui-tabs-panel').find('.paper-id');
				paper.html("");
				for (var i = 0; i < ui.item.papers.length; i++) {
					paper.append($("<option></option>").attr("value", ui.item.papers[i].value).text(ui.item.papers[i].label));
				}

				return false;
			}
		});
	});

	$('#tabs input[type=button]').click(function (e) {
		disableWithLoading(true);

		$('.errors').hide();
		$('.message').hide();

		var element = $(this).parents('.ui-tabs-panel');

		ajaxCall(
			this, 'session/addParticipant',
			{   'session_id': sessionId,
				'participant_id': element.find('.participant-id').val(),
				'type_id': element.find('.type-id').val(),
				'paper_id': element.find('.paper-id').val()
			},
			function (data) {
				disableWithLoading(false);
				$('#tabs').tabs("option", "active", false);
				setParticipantDataForSession(data);
			},
			function () {
				disableWithLoading(false);
			}
		);
	});

	$(document).on("removed-item", '.session-participants .ui-icon-circle-minus', function (e) {
		$('.errors').hide();
		$('.message').hide();

		var element = $(e.target).parents('.participant-type-value');
		var parentElement = element.parents('li');

		ajaxCall(
			this, 'session/deleteParticipant',
			{   'session_id': sessionId,
				'user_id': parentElement.find('.user-id').val(),
				'type_id': element.find('.type-id').val()
			},
			function (data) {
				$('#tabs').tabs("option", "active", false);
				setParticipantDataForSession(data);
			}
		);
	});
});

var disableWithLoading = function (enable) {
	var overlay = $('#loading');

	if (enable) {
		var schedule = $('#tabs');
		var position = schedule.position();

		overlay.css({
			top: position.top,
			left: position.left,
			width: schedule.outerWidth(),
			height: schedule.outerHeight()
		});

		overlay.show();
	}
	else {
		overlay.hide();
	}
}