var networkId = 0;

$(document).ready(function() {
    networkId = parseInt($('input[name=id]').val());

    $('.loading').click(function(e) {
        var element = $(this);

        $.getJSON('../participantsNotScheduled', {network_id: networkId}, function(data) {
            var participantsContainer = $('#not-in-session');

            if (data.participants.length === 0) {
                participantsContainer.text('-');
            }
            else {                
                var clone = participantsContainer.find('li:eq(0)').clone(true);
                participantsContainer.html("");
                
                for (var i=0; i<data.participants.length; i++) {
                    var item = clone.clone(true);
                    item.find('.participant').text(data.participants[i].participant);
                    item.find('.participant').attr('href', data.participants[i].url);

                    var papersContainer = item.find('.participants');
                    var paperClone = papersContainer.find('li');
                    for (var j=0; j<data.participants[i].papers.length; j++) {
                        var paperItem = paperClone.clone(true);
                        paperItem.text(data.participants[i].papers[j].name + " (" + data.participants[i].papers[j].state + ")");
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

    $('.add-session').click(function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(this);

        $.getJSON('../addSession', {network_id: networkId, session_id: element.prev().val()}, function(data) {
            if (data.success) {
                setParticipantDataForNetwork(data);
            }
            else {
                showErrors(data);
            }
        });
    });

    $('.add-new-session').click(function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(this);
        var parentElement = element.parent();

        var code = parentElement.find('.session-code');
        var name = parentElement.find('.session-name');

        $.getJSON('../addSession',
            {   network_id:     networkId,
                session_code:   code.val(),
                session_name:   name.val()},
            function(data) {
                if (data.success) {
                    setParticipantDataForNetwork(data);
                }
                else {
                    showErrors(data);
                }
            }
        );

        code.val("");
        name.val("");
    });

    $(document).on("removed-item", '.remove-session', function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(e.target);
        var parentElement = element.parent();

        $.getJSON('../removeSession',
            {   network_id:     networkId,
                session_id:     parentElement.find('.session-id').val()},
            function(data) {
                if (data.success) {
                    setParticipantDataForNetwork(data);
                }
                else {
                    showErrors(data);
                }
            }
        );
    });
});