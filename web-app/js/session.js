var sessionId = 0;
var participants = [];

var setSessionData = function(data) {
    $('#tabs').tabs("option", "selected", -1);

    if (data.success) {
        var participantsContainer = $('#session-participants');
        var clone = participantsContainer.find('li:first').clone(true);

        participantsContainer.html("");
        if (data.participants.length === 0) {
            var item = clone.clone(true);
            item.find('.user-id').val("");
            item.find('.type-id').val("");
            item.find('.property-value').text("-");
            item.find('.ui-icon-circle-minus').remove();
            participantsContainer.append(item);
        }

        for (var i=0; i<data.participants.length; i++) {
            var item = clone.clone(true);
            item.find('.user-id').val(data.participants[i][0]);
            item.find('.type-id').val(data.participants[i][1]);
            item.find('.paper-ids').val(data.participants[i][2]);
            item.find('.property-value').text(data.participants[i][3]);

            if (item.find('.ui-icon-circle-minus').length === 0) {
                item.append('<span class="ui-icon ui-icon-circle-minus"></span>');
            }

            if (i !== 0) {
                item.find('.property-label').text("");
            }

            participantsContainer.append(item);
        }

        var equipmentContainer = $('#session-equipment');
        clone = equipmentContainer.find('li:first').clone(true);

        equipmentContainer.html("");
        if (data.equipment.length === 0) {
            var item = clone.clone(true);
            item.find('.property-value').text("-");
            equipmentContainer.append(item);
        }

        for (var i=0; i<data.equipment.length; i++) {
            var item = clone.clone(true);
            item.find('.property-value').text(data.equipment[i][0] + " (" + data.equipment[i][1] + ")");

            if (i !== 0) {
                item.find('span.property-label').text("");
            }

            equipmentContainer.append(item);
        }
    }
    else {
        var errorsBox = $('.errors');
        if (errorsBox.length === 0) {
            errorsBox = $('h1').after('<ul class="errors" role="alert"></ul>').next();
        }
        errorsBox.html("");

        if ($.isArray(data.message)) {
            for (var i=0; i<data.message.length; i++) {
                errorsBox.prepend('<li>' + data.message[i] + '</li>');
            }
        }
        else {
            errorsBox.prepend('<li>' + data.message + '</li>');
        }
    }
}

$.getJSON('../participants', function(data) {
    participants = data;
});

$(document).ready(function() {
    sessionId = $('form span:first').text();

    $('#tabs').tabs({
        collapsible: true,
        selected: -1,
        select: function(event, ui) {
            var participantsCopy;
            var selected = $(ui.panel);
            var textBox = selected.find('.select-participant');
            var typeId = selected.find('.type-id').val();

            textBox.autocomplete("option", "source", []);
            textBox.val("");

            $.getJSON(
                '../participantsWithType',
                {   'type_id':      typeId,
                    'session_id':   sessionId},
                function(data) {
                    participantsCopy = $.grep(participants, function(n) {
                        for (var i=0; i<data.length; i++) {
                            if (data[i] === n.value) {
                                return null;
                            }
                        }
                        return n;
                    });

                    textBox.autocomplete("option", "source", participantsCopy);
                }
            );
        }
    });

    $('.select-participant').each(function(e) {
        $(this).autocomplete({
            src: [],
            search: function(event, ui) {
                $(this).parents('.ui-tabs-panel').find('.participant-id').val("");
            },
            focus: function(event, ui) {
                $(this).val(ui.item.label);
                return false;
            },
            select: function(event, ui) {
                var element = $(this);
                element.val(ui.item.label);
                element.parents('.ui-tabs-panel').find('.participant-id').val(ui.item.value);

                var paper = $('.paper-id');
                paper.html("");
                for (var i=0; i<ui.item.papers.length; i++) {
                    paper.append($("<option></option>").attr("value", ui.item.papers[i].value).text(ui.item.papers[i].label));
                }

                return false;
            }
        });
    });

    $('#tabs input[type=button]').click(function(e) {
        var element = $(this).parents('.ui-tabs-panel');

        $.getJSON(
            '../addParticipant',
            {   'session_id':       sessionId,
                'participant_id':   element.find('.participant-id').val(),
                'type_id':          element.find('.type-id').val(),
                'paper_id':         element.find('.paper-id').val()
            },
            setSessionData
        );
    });

    $('.ui-icon-circle-minus').live('click', function(e) {
        var element = $(this).parents('li');
        $.getJSON('../../message/index', {code: 'default.button.delete.confirm.message'}, function(data) {
            var deleted = confirm(data.message);
            if (deleted) {
                $.getJSON(
                    '../deleteParticipant',
                    {   'session_id':   sessionId,
                        'user_id':      element.find('.user-id').val(),
                        'type_id':      element.find('.type-id').val(),
                        'paper_ids':    element.find('.paper-ids').val()
                    },
                    setSessionData
                );
            }
        });
   });
});