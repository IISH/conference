var tabsHeight = 0;
var userId;

var moveTabs = function() {
    var size = 0;
    var errors = $('ul.errors');
    var message = $('div.message');
    
    if ((errors.size() > 0) && !errors.is(':hidden')) {
        size += errors.outerHeight(true) - parseInt(errors.css('margin-top').replace('px', ''));
    }
    if ((message.size() > 0) && !message.is(':hidden')) {
        size += message.outerHeight(true) - parseInt(message.css('margin-top').replace('px', ''));
    }
    
    $('#participant-form #tabs > ul:first-child').css('top', tabsHeight-size + 'px');
}

$(document).ready(function() {
    var tabs = $('#participant-form #tabs > ul:first-child');
    tabsHeight = parseInt(tabs.css('top').replace('px', ''));
    moveTabs();

    userId = parseInt($('input[name=id]').val());
    
    $(document).on("error", function(e) {
        moveTabs();
    });

    $(document).on("message", function(e) {
        moveTabs();
    });
    
    $(document).on("removed-item", '.paper.ui-icon-circle-minus', function(e) {
        var paperId = $(e.target).parents('.column').children('input[type=hidden]:first').val();
        ajaxCall('participant/removePaper', {'paper-id': paperId});

        $(this).parent().text('-');  
    });

    $('#btn_network').click(function(e) {
        var id = $(this).prev().find(':selected').val();
        if ($.isNumeric(id)) {
            window.open('../../network/show/' + id);
        }
    });

    $('#participant-form .btn_add').click(function(e) {
        var noPapers = $('#papers-tab .column').length - 1;
        var maxPapers = null;

        var maxPapersVal = $('input[name=max-papers]').val();
        if ($.isNumeric(maxPapersVal)) {
            maxPapers = parseInt(maxPapersVal);
        }

        if ((maxPapers !== null) && (noPapers >= maxPapers)) {
            e.stopImmediatePropagation();
        }
    });

    $('.order-set-payed').click(function(e) {
        ajaxCall(messageUrl, {code: 'default.button.delete.confirm.message'}, function(data) {
            var setPayed = confirm(data.message);
            if (setPayed) {
                $('.errors').hide();
                $('.message').hide();

                var element = $(e.target).parents('.participant-order');

                ajaxCall('participant/setPayed',
                    {   'user_id':  userId,
                        'order_id': element.find('#order-id-label').next().text()
                    },
                    function(data) {
                        $(e.target).parent().text(data.state).removeClass('orange').addClass('green');
                    }
                );
            }
        });
    });

    $('#emails-not-sent, #emails-sent').accordion({
        header: '.emailHeader',
        heightStyle: 'content',
        collapsible: true,
        active: false,
        beforeActivate: function(event, ui) {
            if (!$.isEmptyObject(ui.newPanel) && (ui.newPanel.children().eq(1).val() == 0)) {
                var emailId = ui.newPanel.children(":first").val();
                ajaxCall('participant/emailDetails', {'email-id': emailId}, function(data) {
                    ui.newPanel.find('#original-sent-label').next().prepend(data.orginalSent);
                    ui.newPanel.find('#copies-sent-label').next().html(data.copiesSent);
                    ui.newPanel.find('#from-label').next().text(data.from);
                    ui.newPanel.find('#subject-label').next().text(data.subject);
                    ui.newPanel.find('#body-label').next().html(data.body);

                    ui.newPanel.children().eq(1).val(1);
                });
            }
        }
    });

    $('.resend-email').click(function(e) {
        var emailId = $(this).parents('.email-content').children(":first").val();
        ajaxCall('participant/resendEmail', {'email-id': emailId}, function(data) {
            showMessage(data);
        });
    });
});