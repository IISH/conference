var content, body, navWidth, contentMargin;

var decodeUrlParameters = function(urlParameters) {
    var parameters = {};
    var re = /([^&=]+)=([^&]*)/g;
    var parameter;

    while (parameter = re.exec(urlParameters)) {
        parameters[decodeURIComponent(parameter[1])] = decodeURIComponent(parameter[2]);
    }

    return parameters;
}

var setContentWidth = function() {
    var bodyWidth = body.outerWidth();
    var newContentWidth = bodyWidth - navWidth - (contentMargin * 2);
    content.css("width", newContentWidth + "px");
}

var showErrors = function(data) {
    var errorsBox = $('.errors');
    errorsBox.hide();

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

    errorsBox.show();
}

var setDatePicker = function(element) {
    element = $(element);

    element.datepicker("destroy");
    element.datepicker({
        dateFormat: element.attr('placeholder').replace('yyyy', 'yy')
    });
}

var createNewItem = function(item, lastItem) {
    var i = -1;
    if ((lastItem.length !== 0) && (lastItem.hasClass('.column') || lastItem.is('li'))) {
        var nameSplit = lastItem.find('input, select, textarea').attr("name").split('.');
        var number = nameSplit[0].split('_')[1];
        if ($.isNumeric(number)) {
            i = number;
        }
    }
    i++;

    var clone = item.clone(true);

    clone.find('input, select, textarea').each(function() {
        var name = $(this).attr("name");

        if (name.indexOf("null") === -1) {
            var nameSplit = name.split("_");
            $(this).attr("name", nameSplit[0] + "_" + i + nameSplit[1]);
            $(this).attr("id", nameSplit[0] + "_" + i + nameSplit[1]);
        }
        else {
            $(this).attr("name", name.replace("null", i));
            $(this).attr("id", name.replace("null", i));
        }
    });

    clone.find('.datepicker').each(function() {
        setDatePicker(this);
    });

    clone.removeClass("hidden");
    return clone;
}

var removeAnItem = function(toBeRemoved, classToStop) {
    var next = toBeRemoved.next();
    while (!next.hasClass(classToStop)) {
        var elements = next.find('input, select')
        var nameSplit = elements.attr("name").split('.');
        var number = nameSplit[0].split('_')[1];
        if ($.isNumeric(number)) {
            var newNumber = number - 1;
            elements.each(function() {
                $(this).attr("name", $(this).attr("name").replace(number, newNumber));
            });
        }
        next = next.next();
    }

    var idsToBeRemoved = toBeRemoved.parents('ul, .columns.copy').find('.to-be-deleted');
    if (idsToBeRemoved.length > 0) {
        var ids = idsToBeRemoved.val().split(';');
        ids.push(toBeRemoved.find('input[type=hidden]:eq(0)').val());
        for (var i=0; i<ids.length; i++) {
            if (ids[i] == undefined || ids[i] == null || ids[i] == "") {
                ids.splice(i, 1);
                i--;
            }
        }
        idsToBeRemoved.val(ids.join(';'));
    }

    toBeRemoved.remove();
}

$(document).ready(function() {
    content = $('#content');
    body = $('body');
    navWidth = $('#nav').outerWidth();
    contentMargin = parseInt(content.css('margin-left').replace('px', ''));
    
    $(window).resize(setContentWidth);
    setContentWidth();
	
    $('#tabs').tabs();

    $('#event_switcher').change(function(e) {
        $(this).parents('form').submit();
    });
	
    $('#loggedin img').click(function(e) {
        $("#usermenu").toggle();
    });

    $('#menu dl').prev().click(function(e) {
        $(this).next().slideToggle('fast');
    });

    $('.export-data').change(function(e) {
        var urlParams = $(this).val();

        if (urlParams != -1) {
            var urlParameters = decodeUrlParameters(window.location.search.substring(1));
            $.extend(urlParameters, decodeUrlParameters(urlParams));

            window.location.search = "?" + $.param(urlParameters);
        }
    });

    $('input[type=submit]').click(function(e) {
        var form = $(this).parents('form');
        var children = form.find('.hidden input, .hidden select, .hidden textarea');
        children.removeAttr('required');
        form.submit();
    });

    $('.datepicker').each(function() {
        setDatePicker(this);
    });

    $('.add > span.ui-icon-circle-plus').click(function(e) {
        var parent = $(this).parent();
        var lastItem = parent.prev();
        var item = parent.next();

        var newItem = createNewItem(item, lastItem);
        newItem.insertBefore(parent);
    });

    $('.buttons .btn_add').click(function(e) {
        var parent = $(this).parent().prev().find('.columns.copy');
        var item = parent.find('.column.hidden');
        var lastItem = item.prev();

        var newItem = createNewItem(item, lastItem);
        newItem.insertBefore(item);
    });    

    $('fieldset li span.ui-icon-circle-minus').click(function(e) {
        removeAnItem($(this).parent(), 'add');
    });

    $('.columns.copy span.ui-icon-circle-minus').click(function(e) {
        removeAnItem($(this).parents('.column'), 'hidden');
    });

    $('.filter input').keypress(function(e) {
        if (e.which == 13) {
            var urlParameters = decodeUrlParameters(window.location.search.substring(1));
            $('.filter input, .filter select').each(function() {
                urlParameters[$(this).attr("name")] = $(this).val();
            });
            window.location.search = "?" + $.param(urlParameters);
        }
    });

    $('.filter select').change(function(e) {
        var urlParameters = decodeUrlParameters(window.location.search.substring(1));
        $('.filter input, .filter select').each(function() {
            urlParameters[$(this).attr("name")] = $(this).val();
        });
        window.location.search = "?" + $.param(urlParameters);
    });

    $('.tbl_container tbody tr').click(function(e) {
        var element = $(this);
        var link = element.parents('.tbl_container').find('input[name=url]').val();
        window.location = link.replace('/0', "/" + element.find("td.id").text().trim());
    });

    $('.check-all').click(function(e) {
        var checked = $(this).is(':checked');
        $(this).parents('.column').find('input[type=checkbox]').attr('checked', checked);
    });
});