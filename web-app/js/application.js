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
    var bodyWidth = body.outerWidth(true);
    var newContentWidth = bodyWidth - navWidth - contentMargin;
    newContentWidth = (newContentWidth < 950) ? 950 : newContentWidth;
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

var setDatePicker = function(element, increaseDay) {
    element = $(element);

    element.datepicker("destroy");
    element.datepicker({
        dateFormat: element.attr('placeholder').replace('yyyy', 'yy')
    });
    
    if (increaseDay && (element.val().length > 0)) {
        var date = element.datepicker('getDate'); 
        date.setDate(date.getDate()+1); 
        element.datepicker('setDate', date);
    }
}

var makeResizable = function(element) {
    element = $(element);
    var maxSize = parseInt(element.css('max-width').replace('px', '')) + 8;
        
    if (!element.is(':hidden')) {
        element.resizable({
            handles: "se",
            maxWidth: maxSize
        });
        element.parent().css("padding-bottom", "0");
    }
}

var createNewItem = function(item, lastItem) {
    var i = -1;
    if ((lastItem.length !== 0) && (lastItem.hasClass('column') || lastItem.is('li'))) {
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
        
        if (name.match(/day$/)) {
            $(this).val(lastItem.find('.datepicker').val());
        }
        if (name.match(/dayNumber$/)) {
            var number = eval(lastItem.find('input[type=number]').val());
            if (number) {
                $(this).val(number+1);
            }
        }        
    });

    clone.find('.datepicker').each(function() {
        hasDate = ($(this).val().length > 0);        
        setDatePicker(this, hasDate);
    });
    
    clone.removeClass("hidden");  
    
    return clone;
}

var removeAnItem = function(toBeRemoved, classToStop) {
    var setIndex = toBeRemoved.find('input[name=set-index]');
    if ((setIndex.length > 0) && (setIndex.val() === 'false')) {
        return;
    }

    var next = toBeRemoved.next();
    while (!next.hasClass(classToStop)) {
        var elements = next.find('input, select')
        var nameSplit = elements.attr("name").split('.');
        var number = nameSplit[0].split('_')[1];
        if ($.isNumeric(number)) {
            var newNumber = number - 1;
            elements.each(function() {
                $(this).attr("name", $(this).attr("name").replace(number, newNumber));

                var id = $(this).attr("id");
                if (id !== undefined && id !== null && id.trim().length > 0) {
                    $(this).attr("id", id.replace(number, newNumber));
                }
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

var guessMessageUrl = function() {
    var urlToCall = '../message/index';
    var url = location.href.replace(location.search, '');

    if ($.isNumeric(url.charAt(url.length-1)) || (url.charAt(url.length-1) === '#')) {
        urlToCall = '../' + urlToCall;
    }

    return urlToCall;
}

$(document).ready(function() {
    content = $('#content');
    body = $('body');
    navWidth = $('#nav').outerWidth(true);
    contentMargin = parseInt(content.css('margin-left').replace('px', '')) * 2;
    
    $(window).resize(setContentWidth);
    setContentWidth();
    
    var cookieValue = $.cookie("submenus");
    var openSubMenus = (cookieValue) ? cookieValue.split(';') : [];
    $('#menu dl.sub-menu').each(function() {
        var id = $(this).prev().find('a').attr('href').substring(1);
        
        if ($.isNumeric(id) && ($.inArray(id, openSubMenus) !== -1)) {
            $(this).show();
        }
    });
    
    $('textarea').each(function() {
        makeResizable(this);
    });
    
    $('#tabs').tabs({
        active: $.cookie("tab"),
        activate: function(e, ui) {
            $.cookie("tab", ui.newTab.index());
        }
    });
    
    $('#event_switcher').change(function(e) {
        $(this).parents('form').submit();
    });

    $('html').click(function() {
        $("#usermenu").hide();
     });

    $('#loggedin img').click(function(e) {
        $("#usermenu").toggle();
        e.stopPropagation();
    });

    $('#menu dl.sub-menu').prev().click(function(e) {
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
        
        newItem.find('textarea').each(function() {
            makeResizable(this);
        });
    });

    $('.buttons .btn_add').click(function(e) {
        var parent = $(this).parent().prev().find('.columns.copy');
        var item = parent.find('.column.hidden');
        var lastItem = item.prev();

        var newItem = createNewItem(item, lastItem);
        newItem.insertBefore(item);
        
        newItem.find('textarea').each(function() {
            makeResizable(this);
        });
    });

    $('fieldset li span.ui-icon-circle-minus').live('click', function(e) {
        var thisItem = $(this);
        var item = $(this).parents('li');

        $.getJSON(guessMessageUrl(), {code: 'default.button.delete.confirm.message'}, function(data) {
            var deleted = confirm(data.message);
            if (deleted) {
                if (!thisItem.hasClass('no-del')) {
                    removeAnItem(item, 'add');
                }
                thisItem.trigger('removed-item');
            }
        });
    });

    $('.columns.copy span.ui-icon-circle-minus').live('click', function(e) {
        var thisItem = $(this);
        var item = $(this).parents('.column');

        $.getJSON(guessMessageUrl(), {code: 'default.button.delete.confirm.message'}, function(data) {
            var deleted = confirm(data.message);
            if (deleted) {
                if (!thisItem.hasClass('no-del')) {
                    removeAnItem(item, 'hidden');
                }
                thisItem.trigger('removed-item');
            }
        });
    });

    $('a.btn_delete').click(function(e) {
        e.preventDefault();
        var thisItem = $(this);

        $.getJSON(guessMessageUrl(), {code: 'default.button.delete.confirm.message'}, function(data) {
            var deleted = confirm(data.message);
            if (deleted) {
                window.location = thisItem.attr('href');
            }
        });
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