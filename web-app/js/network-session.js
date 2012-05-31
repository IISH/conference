var sessionId = 0;
var participants = [];

var setSessionData = function(data) {
    $('#tabs').tabs("option", "selected", -1);

    var errorsBox = $('.errors');
    errorsBox.hide();

    if (data.success) {
        var participantsContainer = $('.session-participants');
        var clone = participantsContainer.find('li.hidden').clone(true);
        var item;

        participantsContainer.html("");
        for (var i=0; i<data.participants.length; i++) {
            item = clone.clone(true);
            item.find('.user-id').val(data.participants[i].id);
            item.find('.participant-value').text(data.participants[i].participant);
            item.find('.participant-state-value').text("("+data.participants[i].state+")");

            if (data.participants[i].paper.trim().length === 0) {
                item.find('.participant-paper-value').remove();
            }
            else {
                item.find('.participant-paper-value').text(data.participants[i].paper);
            }

            var typeContainer = item.find('ul');
            var typeClone = typeContainer.find('.participant-type-value');
            var typeItem;
            for (var j=0; j<data.participants[i].types.length; j++) {
                typeItem = typeClone.clone(true);
                typeItem.find('.type-id').val(data.participants[i].types[j].id);
                typeItem.find('.participant-type-val').text(data.participants[i].types[j].type);
                typeContainer.append(typeItem);
            }
            typeClone.remove();

            participantsContainer.append(item);
            item.removeClass('hidden');
        }

        item = clone.clone(true);
        participantsContainer.append(item);
        item.addClass('hidden');

        var equipmentContainer = $('#session-equipment');
        equipmentContainer.html("");
        for (var i=0; i<data.equipment.length; i++) {
            equipmentContainer.append("<li>" + data.equipment[i][0] + " (" + data.equipment[i][1] + ")" + "</li>");
        }
    }
    else {
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

    $('.session-participants .ui-icon-circle-minus').live('click', function(e) {
        var element = $(this).parents('.participant-type-value');
        var parentElement = $(this).parents('li');

        $.getJSON('../../message/index', {code: 'default.button.delete.confirm.message'}, function(data) {
            var deleted = confirm(data.message);
            if (deleted) {
                $.getJSON(
                    '../deleteParticipant',
                    {   'session_id':   sessionId,
                        'user_id':      parentElement.find('.user-id').val(),
                        'type_id':      element.find('.type-id').val()
                    },
                    setSessionData
                );
            }
        });
    });

    $('.loading').click(function(e) {
        var element = $(this);

        $.getJSON('../participantsNotScheduled', {network_id: $(this).find('input').val()}, function(data) {
            var participantsContainer = $('#not-in-session');
            var clone = participantsContainer.find('li:eq(0)')
            var item;

            participantsContainer.html("");
            for (var i=0; i<data.participants.length; i++) {
                item = clone.clone(true);
                item.find('.participant').text(data.participants[i].participant);
                item.find('.participant').attr('href', data.participants[i].url);

                var papersContainer = item.find('.participants');
                var paperClone = papersContainer.find('li');
                var paperItem;
                for (var j=0; j<data.participants[i].papers.length; j++) {
                    paperItem = paperClone.clone(true);
                    paperItem.text(data.participants[i].papers[j].name + " (" + data.participants[i].papers[j].state + ")");
                    papersContainer.append(paperItem);
                }
                paperClone.remove();

                participantsContainer.append(item);
            }
            clone.remove();
            element.remove();
            participantsContainer.show();
        });
    });
});