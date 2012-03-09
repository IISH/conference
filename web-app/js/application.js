$(document).ready(function() {
    $('#lang').change(function() {
        window.location = '?lang='+this.value
    });

    $('#nav .right img').click(function() {
        $("#user_menu").toggle();
    });
});