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

    $('.order-refund-payment').click(function(e) {
        ajaxCall(messageUrl, {code: 'default.button.delete.confirm.message'}, function(data) {
            var refundPayment = confirm(data.message);
            if (refundPayment) {
                $('.errors').hide();
                $('.message').hide();

                var element = $(e.target).parents('.participant-order');

                ajaxCall('participant/refundPayment', {
                        'order_id': element.find('#order-id-label').next().text()
                    }, function(data) {
                        $(e.target).parent().text(data.state);
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

    $('.change-present-days').click(function() {
        $('#edit-days').dialog('open');
    });

    $('#edit-days').dialog({
        autoOpen: false,
        modal: true,
        minWidth: 250,
        minHeight: 200,
        title: "Change days",
        buttons: {
            "Save" : function() {
                var dialog = $(this);
                var days = [];

                var userId = dialog.find('input[name=user-id]').val();
                dialog.find('input[name=day]:checked').each(function() {
                    days.push($(this).val());
                });

                ajaxCall('participant/changeDays', {'user-id': userId, 'days': days.join(';')},
                    function(data) {
                        $('#selected-days .not-found').remove();
                        $('#selected-days ol.list-days-present').remove();

                        if ($.isArray(data.daysPresent)) {
                            var htmlDaysPresent = '';
                            for (var i=0; i<data.daysPresent.length; i++) {
                                htmlDaysPresent += '<li>' + data.daysPresent[i] + '</li>';
                            }

                            $('#selected-days .header').after('<ol class="list-days-present">' + htmlDaysPresent + '</ol>');
                        }
                        else {
                            $('#selected-days .header').after('<span class="not-found">' + data.daysPresent + '</span>');
                        }

                        dialog.dialog("close");
                    },
                    function(data) {
                        alert(data.message);
                    }
                );
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        }
    });
});
