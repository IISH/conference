var sessionId = 0;
var participants = [];

$.getJSON('../participants', function(data) {
    participants = data;
});

$(document).ready(function() {
    sessionId = parseInt($('input[name=id]').val());

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
        $('.errors').hide();
        $('.message').hide();

        var element = $(this).parents('.ui-tabs-panel');

        $.getJSON(
            '../addParticipant',
            {   'session_id':       sessionId,
                'participant_id':   element.find('.participant-id').val(),
                'type_id':          element.find('.type-id').val(),
                'paper_id':         element.find('.paper-id').val()
            },
            function(data) {
                if (data.success) {
                    $('#tabs').tabs("option", "selected", -1);
                    setParticipantDataForSession(data);
                }
                else {
                    showErrors(data);
                }
            }
        );
    });

    $('.session-participants .ui-icon-circle-minus').live('click', function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(this).parents('.participant-type-value');
        var parentElement = $(this).parents('li');

        $.getJSON(
            '../deleteParticipant',
            {   'session_id':   sessionId,
                'user_id':      parentElement.find('.user-id').val(),
                'type_id':      element.find('.type-id').val()
            },
            function(data) {
                if (data.success) {
                    $('#tabs').tabs("option", "selected", -1);
                    setParticipantDataForSession(data);
                }
                else {
                    showErrors(data);
                }
            }
        );
    });
});