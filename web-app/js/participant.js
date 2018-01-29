var tabsHeight = 0;
var userId;

var moveTabs = function () {
	var size = 0;
	var errors = $('ul.errors');
	var message = $('div.message');

	if ((errors.size() > 0) && !errors.is(':hidden')) {
		size += errors.outerHeight(true) - parseInt(errors.css('margin-top').replace('px', ''));
	}
	if ((message.size() > 0) && !message.is(':hidden')) {
		size += message.outerHeight(true) - parseInt(message.css('margin-top').replace('px', ''));
	}

	$('#participant-form').find('#tabs > ul:first-child').css('top', tabsHeight - size + 'px');
};

$(document).ready(function () {
	var tabs = $('#participant-form').find('#tabs > ul:first-child');
	tabsHeight = parseInt(tabs.css('top').replace('px', ''));
	moveTabs();

	userId = parseInt($('input[name=id]').val());

	$(document).on("error", function (e) {
		moveTabs();
	});

	$(document).on("message", function (e) {
		moveTabs();
	});

	$(document).on("removed-item", '.paper.ui-icon-circle-minus', function (e) {
		var paperId = $(e.target).parents('.column').children('input[type=hidden]:first').val();
		ajaxCall(this, 'participant/removePaper', {'paper-id': paperId});

		$(this).parent().text('-');
	});

	$('#btn_network').click(function (e) {
		var id = $(this).prev().find(':selected').val();
		if ($.isNumeric(id)) {
			window.open('../../network/show/' + id);
		}
	});

	$('#participant-form').find('.btn_add').on('before-add-item', function (e, infoObj) {
		var noPapers = $('#papers-tab').find('.column').length - 1;
		var maxPapers = null;

		var maxPapersVal = $('input[name=max-papers]').val();
		if ($.isNumeric(maxPapersVal)) {
			maxPapers = parseInt(maxPapersVal);
		}

		if ((maxPapers !== null) && (noPapers >= maxPapers)) {
			infoObj.addNewItem = false;
			e.stopImmediatePropagation();
			return false;
		}
		else {
			$('#tabs').tabs('option', 'active', 1);
		}
	});

	$('.order-set-payed').click(function (e) {
		var elem = this;
		ajaxCall(elem, messageUrl, {code: 'default.button.confirm.message'}, function (data) {
			var setPayed = confirm(data.message);
			if (setPayed) {
				$('.errors').hide();
				$('.message').hide();

				var element = $(e.target).parents('.participant-order');

				ajaxCall(elem, 'participant/setPayed',
					{   'user_id': userId,
						'order_id': element.find('#order-id-label').next().text()
					},
					function (data) {
						$(e.target).parent().text(data.state).removeClass('orange').addClass('green');
					}
				);
			}
		});
	});

	$('.order-refund-payment').click(function (e) {
		var elem = $(this);
		if (elem.hasClass('refund-dialog')) {
			var amount = parseInt(elem.data('amount')) / 100;

			var dialog = $('#refund-payment');
			dialog.data('org-btn', elem);
			dialog.find('input[name=amount]').val(amount);

			dialog.dialog('open');
		}
		else {
			ajaxCall(elem, messageUrl, {code: 'default.button.confirm.message'}, function (data) {
				var shouldRefundPayment = confirm(data.message);
				if (shouldRefundPayment) {
					refundPayment(elem, elem);
				}
			});
		}
	});

	var refundPayment = function (orgBtn, trigger, amount, callback) {
		$('.errors').hide();
		$('.message').hide();

		var data = {'order_id': orgBtn.data('order-id')};
		if (amount) {
			data.amount = amount;
		}

		ajaxCall(trigger, 'participant/refundPayment', data, function (data) {
			orgBtn.parent().text(data.state);

			if (callback) {
				callback();
			}
		}, function () {
			if (callback) {
				callback();
			}
		});
	};

	$('#emails-not-sent, #emails-sent').accordion({
		header: '.emailHeader',
		heightStyle: 'content',
		collapsible: true,
		active: false,
		beforeActivate: function (event, ui) {
			if (!$.isEmptyObject(ui.newPanel) && (ui.newPanel.children().eq(1).val() == 0)) {
				var emailId = ui.newPanel.children(":first").val();
				ajaxCall(this, 'participant/emailDetails', {'email-id': emailId}, function (data) {
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

	$('.resend-email').click(function (e) {
		var emailId = $(this).parents('.email-content').children(":first").val();
		ajaxCall(this, 'participant/resendEmail', {'email-id': emailId}, function (data) {
			showMessage(data);
		});
	});

    $('.resend-registration-email').click(function (e) {
        var userId = $('input[name=user-id]').val();
        ajaxCall(this, 'participant/resendRegistrationEmail', {'user-id': userId}, function (data) {
            showMessage(data);
        });
    });

	$('.change-present-days').click(function () {
		$('#edit-days').dialog('open');
	});

    $('.change-paper-review').click(function () {
        var elem = $(this);
        var dialog = $('#paper-review');
        dialog.find('input,textarea').val('');
        elem.closest('li').find('input').each(function () {
			var name = $(this).attr('name').split('.')[1];
			var dialogElem = dialog.find('[name=' + name + ']');
            dialogElem.is(':checkbox')
				? dialogElem.prop('checked', $(this).val() === 'true') : dialogElem.val($(this).val());
            dialog.data('paper-review', elem.closest('li'));
        });
		dialog.dialog('open');
    });

	$('#edit-days').dialog({
		autoOpen: false,
		modal: true,
		minWidth: 250,
		minHeight: 200,
		title: "Change days",
		buttons: {
			"Save": function () {
				var dialog = $(this);
				var days = [];

				var userId = dialog.find('input[name=user-id]').val();
				dialog.find('input[name=day]:checked').each(function () {
					days.push($(this).val());
				});

				ajaxCall(this, 'participant/changeDays', {'user-id': userId, 'days': days.join(';')},
					function (data) {
						var selectedDays = $('#selected-days');
						selectedDays.find('.not-found').remove();
						selectedDays.find('ol.list-days-present').remove();

						if ($.isArray(data.daysPresent)) {
							var htmlDaysPresent = '';
							for (var i = 0; i < data.daysPresent.length; i++) {
								htmlDaysPresent += '<li>' + data.daysPresent[i] + '</li>';
							}

							selectedDays.find('.header').after('<ol class="list-days-present">' + htmlDaysPresent + '</ol>');
						}
						else {
							selectedDays.find('.header').after('<span class="not-found">' + data.daysPresent + '</span>');
						}

						dialog.dialog("close");
					},
					function (data) {
						alert(data.message);
					}
				);
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		}
	});

	$('#refund-payment').dialog({
		autoOpen: false,
		modal: true,
		minWidth: 250,
		minHeight: 130,
		title: "Refund payment",
		buttons: {
			"Save": function () {
				var dialog = $(this);
				var orgBtn = dialog.data('org-btn');
				var amount = dialog.find('input[name=amount]').val();

				refundPayment(orgBtn, dialog, amount, function () {
					dialog.dialog("close");
				});
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		}
	});

	$('.btn_add_order').click(function () {
		$('#tabs').tabs('option', 'active', 3);
		$('#new-order').dialog('open');
	});

	$('#new-order').dialog({
		autoOpen: false,
		modal: true,
		minWidth: 410,
		minHeight: 250,
		width: 510,
		height: 300,
		title: "Create new order",
		buttons: {
			"Save": function () {
				$('#new-order-form').submit();
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		}
	});

    $('#paper-review').dialog({
        autoOpen: false,
        modal: true,
        minWidth: 400,
        minHeight: 350,
        width: 500,
        height: 400,
        title: "Paper review",
        buttons: {
            "Save": function () {
                var dialog = $(this);
                var paperReviewElem = dialog.data('paper-review');

                var data = {};
                dialog.find('input, textarea').each(function () {
                	if ($(this).is('[type=checkbox]')) {
                        data[$(this).attr('name')] = $(this).is(':checked');
                        paperReviewElem.find('input[name$=' + $(this).attr('name') + ']').val(
                        	$(this).is(':checked') ? 'true' : 'false'
						);
					}
                	else {
                        data[$(this).attr('name')] = $(this).val();
                        paperReviewElem.find('input[name$=' + $(this).attr('name') + ']').val($(this).val());
                    }
                });

                ajaxCall(this, 'paper/updateReview', data, function (data) {
                    paperReviewElem.find('.avg-score').text(data.avgScore);
                    dialog.dialog("close");
                });
            },
            Cancel: function () {
                $(this).dialog("close");
            }
        }
    });
});
