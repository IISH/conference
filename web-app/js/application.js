var content, body, navWidth, contentMargin, emailValue;

var messageUrl = 'ajax/message';
var uniqueEmailUrl = 'ajax/uniqueEmail';

var decodeUrlParameters = function (urlParameters) {
	var parameters = {};
	var re = /([^&=]+)=([^&]*)/g;
	var parameter;

	while (parameter = re.exec(urlParameters)) {
		parameters[decodeURIComponent(parameter[1])] = decodeURIComponent(parameter[2]);
	}

	return parameters;
};

var setContentWidth = function () {
	var bodyWidth = body.outerWidth(true);
	var newContentWidth = bodyWidth - navWidth - contentMargin;
	newContentWidth -= 15; // Add extra space if scrollbar appears
	newContentWidth = (newContentWidth < 950) ? 950 : newContentWidth;
	content.css("width", newContentWidth + "px");
};

var showErrors = function (data) {
	var errorsBox = $('.errors');
	errorsBox.hide();

	if (errorsBox.length === 0) {
		errorsBox = $('h1').after('<ul class="errors" role="alert"></ul>').next();
	}
	errorsBox.html("");

	if ($.isArray(data.message)) {
		for (var i = 0; i < data.message.length; i++) {
			errorsBox.prepend('<li>' + data.message[i] + '</li>');
		}
	}
	else {
		errorsBox.prepend('<li>' + data.message + '</li>');
	}

	errorsBox.show();
	errorsBox.trigger('error');
};

var showMessage = function (data) {
	var messageBox = $('.message');
	messageBox.hide();

	if (messageBox.length === 0) {
		messageBox = $('h1').after('<div class="message" role="status"></div>').next();
	}
	messageBox.html("");

	messageBox.text(data.message);
	messageBox.show();
	messageBox.trigger('message');
}

var setDatePicker = function (element, increaseDay) {
	element = $(element);

	element.datepicker("destroy");
	element.datepicker({
		dateFormat: element.attr('placeholder').replace('yyyy', 'yy')
	});

	if (element.val().match(/sd$/)) {
		element.val(element.val().replace('sd', ''));
	}
	else if (increaseDay && (element.val().length > 0)) {
		var date = element.datepicker('getDate');
		date.setDate(date.getDate() + 1);
		element.datepicker('setDate', date);
	}
};

var addAutoComplete = function (element) {
	// Have to use listener for auto complete added to dynamically added input boxes
	$(element).on('focus', function () {
		$(this).autocomplete({
			minLength: 3,
			source: function (request, response) {
				var queryName = $(this.element).prevAll(".ac-query").val();

				$.getJSON(guessUrl('user/usersAutoComplete'), {query: queryName, terms: request.term}, function (data) {
					response(data);
				});
			},
			search: function (event, ui) {
				$(event.target).prevAll(".ac-value").val("");
			},
			focus: function (event, ui) {
				$(event.target).val(ui.item.label);
				return false;
			},
			select: function (event, ui) {
				$(event.target).val(ui.item.label);
				$(event.target).prevAll(".ac-value").val(ui.item.value);
				return false;
			}
		});
	});
};

var makeResizable = function (element) {
	element = $(element);
	var maxSize = parseInt(element.css('max-width').replace('px', '')) + 8;

	if (!element.is(':hidden')) {
		element.resizable({
			handles: "se",
			maxWidth: maxSize
		});
		element.parent().css("padding-bottom", "0");
	}
};

var createNewItem = function (item, lastItem) {
	var i = -1;
	if ((lastItem.length !== 0) && (lastItem.hasClass('column') || lastItem.is('li'))) {
		var nameSplit = lastItem.find('input[name], select[name], textarea[name]').attr("name").split('.');
		var number = nameSplit[0].split('_')[1];
		if ($.isNumeric(number)) {
			i = number;
		}
	}
	i++;

	var clone = item.clone(true);

	clone.find('input[name], select[name], textarea[name]').each(function () {
		var name = $(this).attr("name");

		if (name.indexOf("null") === -1) {
			var nameSplit = name.split("_");
			if (nameSplit.length > 1) {
				$(this).attr("name", nameSplit[0] + "_" + i + nameSplit[1]);
				$(this).attr("id", nameSplit[0] + "_" + i + nameSplit[1]);
			}
		}
		else {
			$(this).attr("name", name.replace("null", i));
			$(this).attr("id", name.replace("null", i));
		}

		if (name.match(/day$/)) {
			$(this).val(lastItem.find('.datepicker').val());
			if ($(this).val().trim() === "") {
				var startDate = item.parents('.form').find('input[name$=startDate]');
				$(this).val(startDate.val() + "sd");
			}
		}
		if (name.match(/dayNumber$/)) {
			var number = eval(lastItem.find('input[type=number]').val());
			if (number) {
				$(this).val(number + 1);
			}
			else {
				$(this).val(1);
			}
		}
	});

	clone.find('.datepicker').each(function () {
		hasDate = ($(this).val().length > 0);
		setDatePicker(this, hasDate);
	});

	clone.find('.users-autocomplete').each(function () {
		addAutoComplete(this);
	});

	clone.removeClass("hidden");

	return clone;
};

var removeAnItem = function (toBeRemoved, classToStop) {
	var setIndex = toBeRemoved.find('input[name=set-index]');
	if ((setIndex.length > 0) && (setIndex.val() === 'false')) {
		return;
	}

	var next = toBeRemoved.next();
	while (!next.hasClass(classToStop)) {
		var elements = next.find('input[name], select[name]')
		var nameSplit = elements.attr("name").split('.');
		var number = nameSplit[0].split('_')[1];
		if ($.isNumeric(number)) {
			var newNumber = number - 1;
			elements.each(function () {
				if ($(this).attr("name") !== undefined) {
					$(this).attr("name", $(this).attr("name").replace(number, newNumber));

					var id = $(this).attr("id");
					if (id !== undefined && id !== null && id.trim().length > 0) {
						$(this).attr("id", id.replace(number, newNumber));
					}
				}
			});
		}
		next = next.next();
	}

	var idsToBeRemoved = toBeRemoved.parents('ul, .columns.copy').find('.to-be-deleted');
	if (idsToBeRemoved.length > 0) {
		var ids = idsToBeRemoved.val().split(';');
		ids.push(toBeRemoved.find('input[type=hidden]:eq(0)').val());
		for (var i = 0; i < ids.length; i++) {
			if (ids[i] == undefined || ids[i] == null || ids[i] == "") {
				ids.splice(i, 1);
				i--;
			}
		}
		idsToBeRemoved.val(ids.join(';'));
	}

	toBeRemoved.remove();
};

var guessUrl = function (urlToCall) {
	var url = location.href.replace(location.search, '').replace(location.hash, '').replace('#', '');

	urlToCall = (urlToCall.indexOf('/') === 0) ? urlToCall.substring(1) : urlToCall;
	urlToCall = (urlToCall.indexOf('../') === 0) ? urlToCall : '../' + urlToCall;

	if ($.isNumeric(url.charAt(url.length - 1))) {
		urlToCall = '../' + urlToCall;
	}

	return urlToCall;
}

var ajaxCall = function (element, url, params, onSuccess, onFailure) {
	element = $(element);
	if (element.hasClass('ajax-disabled')) {
		return;
	}

	var clickedButton = element.is("input[type='button']") || element.hasClass('inline-button');
	if (clickedButton) {
		element.attr('disabled', 'disabled');
		element.addClass('ajax-disabled');
		element.after('<span class="ajax-loading">Please wait!</span>');
	}

	$('.errors').hide();
	$('.message').hide();

	$.getJSON(guessUrl(url), params, function (data) {
		if (clickedButton) {
			element.attr('disabled', '');
			element.removeClass('ajax-disabled');

			var next = element.next();
			if (next.hasClass('ajax-loading')) {
				next.remove();
			}
		}

		if (!data.success) {
			showErrors(data);

			if ($.isFunction(onFailure)) {
				onFailure(data);
			}
		}
		else if ($.isFunction(onSuccess)) {
			onSuccess(data);
		}
	}).fail(function (jqXHR, textStatus, error) {
		if (jqXHR.status === 401) {
			location.reload();
		}

		if (clickedButton) {
			element.attr('disabled', '');
			element.removeClass('ajax-disabled');

			var next = element.next();
			if (next.hasClass('ajax-loading')) {
				next.remove();
			}
		}

		var data = {message: error};
		showErrors(data);

		if ($.isFunction(onFailure)) {
			onFailure(data);
		}
	});
};

var subMenusToOpen = function(openSubMenus) {
    $('#menu dl.sub-menu').each(function () {
        var id = $(this).prev().find('a').attr('href').substring(1);
        $(this).toggle((openSubMenus === 'all') || ($.isNumeric(id) && ($.inArray(id, openSubMenus) !== -1)));
    });
};

$(document).ready(function () {
	content = $('#content');
	body = $('body');
	navWidth = $('#nav').outerWidth(true);
	contentMargin = parseInt(content.css('margin-left').replace('px', '')) * 2;

	$(window).resize(setContentWidth);
	setContentWidth();

	$('#loading').hide();

	$.Placeholder.init();

	var cookieValue = $.cookie("submenus");
	var openSubMenus = (cookieValue) ? cookieValue.split(';') : [];
    subMenusToOpen(openSubMenus);

	$('textarea').each(function () {
		makeResizable(this);
	});

	$('#tabs').tabs({
		active: $.cookie("tab"),
		activate: function (e, ui) {
			$.cookie("tab", ui.newTab.index());
		}
	});

	var urlParameters = decodeUrlParameters(window.location.search.substring(1));
	$('.sort_asc, .sort_desc').each(function () {
		var element = $(this);
		var values = element.attr("name").split('|');

		var order = element.hasClass("sort_asc") ? "asc" : "desc";
		var paramName = "sort_" + values[0];
		if (urlParameters[paramName]) {
			var sortedFields = urlParameters[paramName].split(';');
			if ($.inArray(values[1] + ":" + order, sortedFields) >= 0) {
				element.removeClass(order + "_unselected");
				element.addClass(order + "_selected");
			}
		}
	});

	$('#event_switcher').change(function (e) {
		$(this).parents('form').submit();
	});

	$('html').click(function (e) {
		$(".menu").hide();
		document.oncontextmenu = null;
	});

	$('#loggedin img').click(function (e) {
		$("#usermenu").toggle();
		e.stopPropagation();
	});

	$("select[multiple] option").mousedown(function () {
		if ($(this).attr("selected")) {
			$(this).attr("selected", "");
		}
		else {
			$(this).attr("selected", "selected");
		}
	});

	$('#menu dl.sub-menu').prev().click(function (e) {
		var subMenu = $(this).next();
		var id = $(this).find('a').attr('href').substring(1);
		var cookieValue = $.cookie("submenus");
		var openSubMenus = (cookieValue) ? cookieValue.split(';') : [];

		var index = $.inArray(id, openSubMenus);
		if (subMenu.is(':hidden') && (index === -1)) {
			openSubMenus.push(id);
		}
		else if (index !== -1) {
			openSubMenus.splice(index, 1);
		}

		$.cookie("submenus", openSubMenus.join(';'), {path: '/'});
		subMenu.slideToggle('fast');
	});

    $('#menu-filter').keyup(function(e) {
        var text = $(this).val().toLowerCase().trim();
        var submenus = $([]);

        $('#menu .menu-item').each(function() {
            if ($(this).next().hasClass('sub-menu')) {
                submenus = submenus.add(this);
            }
            else {
                $(this).toggle($(this).text().toLowerCase().indexOf(text) >= 0);
            }
        });

        if (text.length > 0) {
            subMenusToOpen('all');
            submenus.each(function() {
                var show = ($(this).next().children(':visible').length > 0);
                $(this).next().toggle(show);
                $(this).toggle(show);
            });
        }
        else {
            subMenusToOpen(openSubMenus);
            submenus.show();
        }
    });

	$('.export-data').change(function (e) {
		var urlParams = $(this).val();

		if (urlParams != -1) {
			var urlParameters = decodeUrlParameters(window.location.search.substring(1));
			$.extend(urlParameters, decodeUrlParameters(urlParams));

			window.location.search = "?" + $.param(urlParameters);
		}
	});

	$('input[type=submit]').click(function (e) {
		var form = $(this).parents('form');
		form.find('.hidden input, .hidden select, .hidden textarea').remove();
		form.find('.moveSelectBox.primary').find('option').prop('selected', true);

		$.Placeholder.cleanBeforeSubmit();
		this.click();
	});

	$('.datepicker').each(function () {
		setDatePicker(this);
	});

	$('.add > span.ui-icon-circle-plus').click(function (e) {
		var parent = $(this).parent();
		var lastItem = parent.prev();
		var item = parent.next();

		var newItem = createNewItem(item, lastItem);
		newItem.insertBefore(parent);

		newItem.find('textarea').each(function () {
			makeResizable(this);
		});
	});

	$('.buttons .btn_add').click(function (e) {
		var infoObj = {addNewItem: true};
		$(e.target).trigger('before-add-item', infoObj);

		if (infoObj.addNewItem) {
			var parent = $(this).parent().prev().find('.columns.copy');
			var item = parent.find('.column.hidden');
			var lastItem = item.prev();

			var newItem = createNewItem(item, lastItem);
			newItem.insertBefore(item);

			newItem.find('textarea').each(function () {
				makeResizable(this);
			});
		}
	});

	$(document).on('click', 'fieldset li span.ui-icon-circle-minus', function (e) {
		var thisItem = $(e.target);
		var item = thisItem.parents('li');

		ajaxCall(this, messageUrl, {code: 'default.button.delete.confirm.message'}, function (data) {
			var deleted = confirm(data.message);
			if (deleted) {
				if (!thisItem.hasClass('no-del')) {
					removeAnItem(item, 'add');
				}
				thisItem.trigger('removed-item');
			}
		});
	});

	$(document).on('click', '.columns.copy span.ui-icon-circle-minus', function (e) {
		var thisItem = $(e.target);
		var item = thisItem.parents('.column');

		ajaxCall(this, messageUrl, {code: 'default.button.delete.confirm.message'}, function (data) {
			var deleted = confirm(data.message);
			if (deleted) {
				if (!thisItem.hasClass('no-del')) {
					removeAnItem(item, 'hidden');
				}
				thisItem.trigger('removed-item');
			}
		});
	});

	$('a.btn_delete').click(function (e) {
		e.preventDefault();
		var thisItem = $(this);

		ajaxCall(this, messageUrl, {code: 'default.button.delete.confirm.message'}, function (data) {
			var deleted = confirm(data.message);
			if (deleted) {
				window.location = thisItem.attr('href');
			}
		});
	});

	$('.filter input').keypress(function (e) {
		if (e.which == 13) {
			var urlParameters = decodeUrlParameters(window.location.search.substring(1));
			$('.filter input, .filter select').each(function () {
				if ($(this).val() === $(this).attr('placeholder')) {
					$(this).val('');
				}
				urlParameters[$(this).attr("name")] = $(this).val();
			});
			window.location.search = "?" + $.param(urlParameters);
		}
	});

	$('.filter select').change(function (e) {
		var urlParameters = decodeUrlParameters(window.location.search.substring(1));
		$('.filter input, .filter select').each(function () {
			if ($(this).val() === $(this).attr('placeholder')) {
				$(this).val('');
			}
			urlParameters[$(this).attr("name")] = $(this).val();
		});
		window.location.search = "?" + $.param(urlParameters);
	});

	$('.sort_asc, .sort_desc').click(function (e) {
		var element = $(this);
		var values = element.attr("name").split('|');
		var urlParameters = decodeUrlParameters(window.location.search.substring(1));

		var order = element.hasClass("sort_asc") ? "asc" : "desc";
		var paramName = "sort_" + values[0];
		var sortedFields = [];
		if (urlParameters[paramName]) {
			sortedFields = urlParameters[paramName].split(';');
		}

		var i = $.inArray(values[1] + ":" + order, sortedFields);
		if (i >= 0) {
			sortedFields.splice(i, 1);
		}
		else {
			sortedFields.push(values[1] + ":" + order);
		}

		var oppositeOrder = (order === "desc") ? "asc" : "desc";
		var j = $.inArray(values[1] + ":" + oppositeOrder, sortedFields);
		if (j >= 0) {
			sortedFields.splice(j, 1);
		}

		urlParameters[paramName] = sortedFields.join(';');
		window.location.search = "?" + $.param(urlParameters);
	});

	$('.tbl_container tbody tr:not(.tbl_totals)').mousedown(function (e) {
		var element = $(this);
		var target = $(e.target);

		if (target[0] === element[0] || target.parent()[0] === element[0]) {
			var container = element.parents('.tbl_container');
			var linkElement = container.find('input[name=url]')

			if (linkElement.size() > 0) {
				var link = linkElement.val();
				var id = element.find("td.id").text().trim();

				link = link.replace('/0', "/" + id);

				switch (e.which) {
					case 1:
						window.location = link;
						break;
					case 3:
						document.oncontextmenu = function () {
							return false;
						};
						e.preventDefault();

						var menu = container.find(".menu");

						menu.find('a').each(function (index, value) {
							$(value).attr('href', link);
						});

						var left = e.pageX - element.offset().left;
						var top = element.position().top;

						menu.css({left: left + "px", top: top + "px"}).show();
				}
			}
		}
	});

	$('.check-all').click(function (e) {
		var checked = $(this).is(':checked');
		$(this).parents('.column').find('input[type=checkbox]').attr('checked', checked);
	});

	var email = $('input[type=email]');
	emailValue = (email.length > 0) ? email.val().toLowerCase().trim() : "";

	$('input[type=email]:not(.no-email-validation)').blur(function (e) {
		var element = $(this);
		var email = element.val().toLowerCase().trim();

		$('.errors').hide();
		element.parent().removeClass('error');

		if (email !== emailValue) {
			ajaxCall(this, uniqueEmailUrl, {email: email}, function (data) {
				if (!data.success) {
					element.parent().addClass('error');
					showErrors(data);
				}
			});
		}

		element.trigger('error');
	});

	$('.tbl_container .session-state-select-tbl').change(function (e) {
		var state_id = $(this).val();
		var column = $(this).parents("td");

		ajaxCall(this, 'session/changeState', {session_id: column.prev().text(), state_id: state_id},
			function () {
				column.find("span.ui-icon-check").css('visibility', 'visible');
			},
			function () {
				column.find("span.ui-icon-alert").css('visibility', 'visible');
			}
		);
	});

	$('.tbl_container .paper-state-select').change(function (e) {
		var state_id = $(this).val();
		var column = $(this).parents("td");

		ajaxCall(this, 'participant/changePaperState', {paper_id: column.prev().text(), state_id: state_id},
			function () {
				column.parent().find("span.ui-icon-check").css('visibility', 'visible');
			},
			function () {
				column.parent().find("span.ui-icon-alert").css('visibility', 'visible');
			}
		);
	});

	$('#edit-paper').dialog({
		autoOpen: false,
		modal: true,
		minWidth: 520,
		minHeight: 220,
		title: "Change paper state",
		buttons: {
			"Save": function () {
				var dialog = $(this);
				var paperId = dialog.find('input[name=paper-id]').val();
				var stateId = dialog.find('input[name=paper-state]:checked').val();

				ajaxCall(this, 'participant/changePaperState', {paper_id: paperId, state_id: stateId},
					function (data) {
						$('.paper-id[value=' + paperId + ']').parent().find('.paper-text').text(data.paper);
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

	$(document).on('click', '.edit-paper-icon', function (e) {
		var dialog = $('#edit-paper');
		var paper = $(this).prev().prev().prev().text();
		var paperId = $(this).prev().prev().val();
		var paperStateId = $(this).prev().val();

		dialog.find("input[name=paper-id]").val(paperId);
		dialog.find(':radio[value=' + paperStateId + ']').attr('checked', true);
		dialog.find(".participant-paper-value").text(paper);

		dialog.dialog('open');
	});

	$('.tbl_container .change-gender').change(function (e) {
		var gender = $(this).val();
		var column = $(this).parents("td");

		ajaxCall(this, 'participant/changeGender', {user_id: column.prev().text(), gender: gender},
			function () {
				column.parent().find("span.ui-icon-check").css('visibility', 'visible');
			},
			function () {
				column.parent().find("span.ui-icon-alert").css('visibility', 'visible');
			}
		);
	});

	$("#network-select, #session-select").change(function (e) {
		$(this).parents('form').submit();
	});

	$('.moveItems').click(function (e) {
		var selectBox = $(this).prev();
		var otherSelectBox = $(".moveSelectBox").not(selectBox);

		selectBox.find('option:selected').remove().appendTo(otherSelectBox);
	});

	$('#preview-options .refresh').click(function (e) {
		var preview = $('#email-preview');
		var templateId = parseInt($('input[name=id]').val());

		ajaxCall(this, 'email/refreshPreview', {id: templateId}, function (data) {
			preview.find('#from-label').next().html(data.from);
			preview.find('#to-label').next().html(data.to);
			preview.find('#subject-label').next().html(data.subject);
			preview.find('#body-label').next().html(data.body);
		});
	});

	$('#btn_session').click(function (e) {
		var id = $(this).prev().find(':selected').val();
		if ($.isNumeric(id)) {
			window.open('../../session/show/' + id);
		}
	});

	$(document).ready(function () {
		$('.users-autocomplete').each(function () {
			addAutoComplete(this);
		});
	});

	$('div.tbl_container.invitation-letters thead tr').append('<td>&nbsp;</td>');
	$('div.tbl_container.invitation-letters tbody tr').append(
			'<td><input type="button" class="send-invitation-letter" value="Send" />' +
			'<span class="ui-icon ui-icon-check invisible"></span>' +
			'<span class="ui-icon ui-icon-alert invisible"></span></td>');

	$(document).on('click', '.send-invitation-letter', function (e) {
		var id = $(e.target).parents("tr").find('td.id').text();
		var column = $(e.target).parents("td");

		ajaxCall(this, 'participant/sendInvitationLetter', {user_id: id},
			function (data) {
				column.find("span.ui-icon-check").css('visibility', 'visible');
				column.prev().text(data.sent);
			},
			function () {
				column.find("span.ui-icon-alert").css('visibility', 'visible');
			}
		);
	});

	$('#participants-export-dialog').dialog({
		autoOpen: false,
		minWidth: 400,
		minHeight: 80,
		title: "Participants export"
	});

	$('.participants-export-open').click(function (e) {
		$('#participants-export-dialog').dialog('open');
	});

	$('#email-codes').accordion({
		header: '.emailCodesHeader',
		heightStyle: 'content',
		collapsible: true,
		active: false
	});
});
