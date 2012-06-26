var networkId = 0;

$(document).ready(function() {
    networkId = parseInt($('input[name=id]').val());

    $('.loading').click(function(e) {
        var element = $(this);

        $.getJSON('../participantsNotScheduled', {network_id: networkId}, function(data) {
            var participantsContainer = $('#not-in-session');
            var clone = participantsContainer.find('li:eq(0)')
            var item;

            participantsContainer.html("");

            if (data.participants.length === 0) {
                participantsContainer.text('-');
            }

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

        $.getJSON('../addSession',
            {   network_id:     networkId,
                session_code:   parentElement.find('.session-code').val(),
                session_name:   parentElement.find('.session-name').val()},
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

    $('.remove-session').bind("removed-item", function(e) {
        $('.errors').hide();
        $('.message').hide();

        var element = $(this);
        var parentElement = element.parent();

        $.getJSON('../removeSession',
            {   network_id:     networkId,
                session_id:   parentElement.find('.session-id').val()},
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