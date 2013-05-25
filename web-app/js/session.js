var sessionId = 0;
var participants = [];
var loaded = false;

$(document).ready(function() {
    sessionId = parseInt($('input[name=id]').val());
        
    $('.select-participant').addClass('ui-autocomplete-loading');
    
    $.getJSON('../participants', function(data) {
        participants = data;
        
        $('.select-participant').each(function(e) {
            $(this).autocomplete({
                source: [],
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
            
            loaded = true;
            disableWithLoading(false);
            $(this).removeClass('ui-autocomplete-loading');
        });
                
        $('#tabs').tabs("enable");
        $('#tabs').on("tabsactivate", tabActivate);        
    });
    
    $('#tabs').tabs({
        collapsible: true,
        active: false,  
        disabled: true     
    });
    
    if (!loaded) {
        disableWithLoading(true);
    }
    
    $('#tabs input[type=button]').click(function(e) {
        disableWithLoading(true);
        
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
                disableWithLoading(false);
                
                if (data.success) {                    
                    $('#tabs').tabs("option", "active", false);
                    setParticipantDataForSession(data);
                }
                else {
                    showErrors(data);
                }
            }
        );
    });

    $(document).on("removed-item", '.session-participants .ui-icon-circle-minus', function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(e.target).parents('.participant-type-value');
        var parentElement = element.parents('li');

        $.getJSON(
            '../deleteParticipant',
            {   'session_id':   sessionId,
                'user_id':      parentElement.find('.user-id').val(),
                'type_id':      element.find('.type-id').val()
            },
            function(data) {
                if (data.success) {
                    $('#tabs').tabs("option", "active", false);
                    setParticipantDataForSession(data);
                }
                else {
                    showErrors(data);
                }
            }
        );
    });
});

var tabActivate = function(event, ui) {
    if (ui.newPanel.length > 0) {
        disableWithLoading(true);
        
        var participantsCopy;
        var selected = $(ui.newPanel);
        var textBox = selected.find('.select-participant');
        var typeId = selected.find('.type-id').val();

        textBox.addClass('ui-autocomplete-loading');
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
                textBox.removeClass('ui-autocomplete-loading');
                disableWithLoading(false);
            }
        );
    }
};

var disableWithLoading = function(enable) {
    var overlay = $('#loading');
    
    if (enable) {
        var schedule = $('#tabs');
        var position = schedule.position();

        overlay.css({
            top:        position.top,
            left:       position.left,
            width:      schedule.outerWidth(),
            height:     schedule.outerHeight()
        });

        overlay.show();
    }
    else {
        overlay.hide();
    }
}