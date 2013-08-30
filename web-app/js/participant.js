var tabsHeight = 0;

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
    
    $(document).on("error", function(e) {
        moveTabs();
    });
    
    $(document).on("removed-item", '.paper.ui-icon-circle-minus', function(e) {
        var paperId = $(e.target).parents('.column').children('input[type=hidden]:first').val();
        ajaxCall('../removePaper', {'paper-id': paperId});

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