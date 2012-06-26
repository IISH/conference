$(document).ready(function() {
    $('.paper.ui-icon-circle-minus').bind("removed-item", function(e) {
        var paperId = $(this).parents('.column').children('input[type=hidden]:first').val();
        $.getJSON('../removePaper', {'paper-id': paperId});

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
});