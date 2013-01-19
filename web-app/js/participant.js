$(document).ready(function() {
    var size = 0;
    var errors = $('ul.errors');
    var message = $('div.message');
    
    if ((errors.size() > 0) && !errors.is(':hidden')) {
        size += errors.outerHeight(true) - parseInt(errors.css('margin-top').replace('px', ''));
    }
    if ((message.size() > 0) && !message.is(':hidden')) {
        size += message.outerHeight(true) - parseInt(message.css('margin-top').replace('px', ''));
    }
    
    var tabs = $('#participant-form #tabs > ul:first-child');
    var tabsHeight = parseInt(tabs.css('top').replace('px', ''));
    tabs.css('top', tabsHeight-size + 'px');
    
    $('.paper.ui-icon-circle-minus').live("removed-item", function(e) {
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