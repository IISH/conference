$(document).ready(function() {
    $('#lang').change(function() {
        window.location = '?lang='+this.value;
    });

    $('#nav .right img').click(function() {
        $("#user_menu").toggle();
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

    $('.btn_cancel, .btn_back').click(function() {
        history.go(-1);
    });

    $('input[name=deleted]:not(:checked)').click(function(e) {
        var checkBox = $(this);
        e.preventDefault();

        $.getJSON('../../message?code=default.button.delete.confirm.message', function(data) {
            var deleted = confirm(data.message);
            checkBox.attr('checked', deleted);
        });
    });
});
