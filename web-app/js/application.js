$(document).ready(function() {
    $('#event_switcher').change(function(e) {
        $(this).parents('form').submit();
    });

    $('#tabs').tabs();

    $('.loggedin img').click(function(e) {
        $("#usermenu").toggle();
    });

    $('input[type=submit]').click(function(e) {
        var form = $(this).parents('form')
        var children = form.find('.hidden input, .hidden select')
        children.removeAttr('required')
        form.submit();
    });

    $('.datepicker').each(function() {
        var dateField = $(this);

       /* $("input[name="+dateField.attr('name')+"_month]").val(new Date(dateField.val()).getMonth() +1);
        $("input[name="+dateField.attr('name')+"_day]").val(new Date(dateField.val()).getDate());
        $("input[name="+dateField.attr('name')+"_year]").val(new Date(dateField.val()).getFullYear());      */

        dateField.datepicker({
            /*onClose: function() {
                $("input[name="+dateField.attr('name')+"_month]").val(new Date(dateField.val()).getMonth() +1);
                $("input[name="+dateField.attr('name')+"_day]").val(new Date(dateField.val()).getDate());
                $("input[name="+dateField.attr('name')+"_year]").val(new Date(dateField.val()).getFullYear());
            }, */
            dateFormat: 'mm/dd/yy'
        });
    });

    $('input[name=deleted]:not(:checked)').click(function(e) {
        var checkBox = $(this);
        e.preventDefault();

        $.getJSON('../../message?code=default.button.delete.confirm.message', function(data) {
            var deleted = confirm(data.message);
            checkBox.attr('checked', deleted);
        });
    });

    $('fieldset span.ui-icon-circle-plus').click(function(e) {
        var parent = $(this).parent();
        var lastItem = parent.prev();
        var clone = parent.next().clone(true);
        var i = 0;

        if (lastItem.length !== 0) {
            var nameSplit = lastItem.find('input, select').attr("name").split('.');
            var number = nameSplit[0].split('_')[1];
            if ($.isNumeric(number)) {
                i = number;
            }
        }
        i++;

        clone.find('input, select').each(function() {
            $(this).attr("name", $(this).attr("name").replace("null", i));
        });

        clone.insertBefore(parent);
        clone.removeClass("hidden");
    });

    $('.buttons .btn_add').click(function(e) {
        var parent = $(this).parent().prev().find('.columns.copy')
        var item = parent.find('.column.hidden')
        var clone = item.clone(true);
        var i = 0;

        if (item.prev().length !== 0) {
            var nameSplit = item.prev().find('input, select').attr("name").split('.');
            var number = nameSplit[0].split('_')[1];
            if ($.isNumeric(number)) {
                i = number;
            }
            i++;
        }

        clone.find('input, select').each(function() {
            $(this).attr("name", $(this).attr("name").replace("null", i));
        });

        clone.insertBefore(item);
        clone.removeClass("hidden");
    });

    $('fieldset li span.ui-icon-circle-minus').click(function(e) {
        var toBeRemoved = $(this).parent();

        var next = toBeRemoved.next();
        while (!next.hasClass('add')) {
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

        toBeRemoved.remove();
    });

    $('.columns.copy span.ui-icon-circle-minus').click(function(e) {
        var toBeRemoved = $(this).parent();

        var next = toBeRemoved.next();
        while (!next.hasClass('hidden')) {
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

        toBeRemoved.remove();
    });

    $('.filter input').keypress(function(e) {
        if (e.which == 13) {
            $('.filter input').each(function() {
                queryParameters[$(this).attr("name")] = $(this).val();
            });
            window.location.search = $.param(queryParameters);
        }
    });

    $('.tbl_container table td').click(function(e) {
        window.location =  "./show/" + $(this).parent().find("td.id").text().trim() + "?" + $.param(queryParameters);
    });

    $('.check-all').click(function(e) {
        var checked = $(this).is(':checked');
        $(this).parents('.column').find('input[type=checkbox]').attr('checked', checked);
    });

    $('#btn_network').click(function() {
        var id = $(this).prev().find(':selected').val();
        if ($.isNumeric(id)) {
            window.open('../../network/show/' + id)
        }
    });

    $('#btn_session').click(function() {
        var id = $(this).prev().val();
        if ($.isNumeric(id)) {
            window.open('../../session/show/' + id)
        }
    });
});

var queryParameters = {}, queryString = location.search.substring(1),
    re = /([^&=]+)=([^&]*)/g, m;

while (m = re.exec(queryString)) {
    queryParameters[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
}