var noShow = true;
var equipment = {};
var timeSlots;
var sessionInfo;
var roomInfo;
var colors = [
    [255, 94, 0], [255, 125, 0], [255, 156, 0], [255, 187, 0], [255, 219, 0],
    [255, 250, 0], [167, 255, 0], [135, 255, 0], [104, 255, 0], [73, 255, 0], [42, 255, 0],
    [10, 255, 0], [0, 255, 21], [0, 255, 52], [0, 255, 83], [0, 255, 114], [0, 255, 146], [0, 255, 177], [0, 255, 208],
    [0, 255, 239], [0, 239, 255], [0, 208, 255], [0, 177, 255], [0, 146, 255], [0, 114, 255], [0, 83, 255], [0, 52, 255],
    [0, 21, 255], [10, 0, 255], [42, 0, 255], [73, 0, 255], [104, 0, 255], [135, 0, 255], [167, 0, 255], [198, 0, 255],
    [229, 0, 255], [255, 0, 250], [255, 0, 219], [255, 0, 187], [255, 0, 156], [255, 0, 125], [255, 0, 94],
    [255, 0, 31], [255, 0, 255]
];

var findIndexesThatMatch = function(equipmentIds) {
    var ids = [];

    for (var equip in equipment) {
        var addIndex = true;
        for (var y=0; y<equipmentIds.length; y++) {
            if ($.inArray(equipmentIds[y], equipment[equip].ids) < 0) {
                addIndex = false;
            }
        }

        if (addIndex) {
            ids.push(equip);
        }
    }

    return ids;
}

var isBestChoice = function(equipmentComboCode, equipmentIds) {
    if (equipmentIds.length === equipment[equipmentComboCode].ids.length) {
        var bestChoice = true;

        for (var y=0; y<equipmentIds.length; y++) {
            var found = false;
            for (var x=0; x<equipment[equipmentComboCode].ids.length; x++) {
                if (equipment[equipmentComboCode].ids[x] === equipmentIds[y]) {
                    found = true;
                }
            }

            if (!found) {
                bestChoice = false;
            }
        }

        return bestChoice;
    }
    else {
        return false;
    }
}

var disableTableWithLoading = function(enable) {
    var overlay = $('#loading');
    var schedule = $('#schedule');

    if (enable) {
        var position = schedule.position();

        overlay.css({
            top:        position.top,
            left:       position.left,
            width:      schedule.outerWidth(),
            height:     schedule.outerHeight()
        });

        overlay.show();
    }
    else {
        overlay.hide();
    }
}

$(document).ready(function() {
    var equipmentCombos = $('.equipment-combos');
    var colorsCount = 0;
    var colorsCountSkip = parseInt(colors.length/(equipmentCombos.length/2));

    equipment[""] = {};
    equipment[""].ids = [];
    equipment[""].css = "#ccc";

    equipmentCombos.each(function() {
        var element = $(this);
        var index = element.find('input[name=equipment-combo-code]').val();

        if (equipment[index] == null || equipment[index] == undefined) {
            equipment[index] = {};
            equipment[index].ids = element.find('input[name=equipment-ids]').val().split(',');

            for (var i=0; i<equipment[index].ids.length; i++) {
                equipment[index].ids[i] = parseInt(equipment[index].ids[i]);
            }

            equipment[index].css = 'rgb('+colors[colorsCount][0]+' ,'+colors[colorsCount][1]+' , '+colors[colorsCount][2]+')';
        }

        element.css('background-color', equipment[index].css);

        colorsCount = colorsCount + colorsCountSkip;
        if (colorsCount > colors.length) {
            colorsCount = 0;
        }
    });

    timeSlots = $('#schedule td.time-slot');

    sessionInfo = $('#session-info-container');
    roomInfo = $('#room-info-container');

    $('.session-block').each(function() {
        $(this).draggable({
            revert: 'invalid',
            helper: 'clone',
            start: function() {
                disableTableWithLoading(true);

                var element = $(this);
                element.hide();
                sessionInfo.hide();
                roomInfo.hide();

                var sessionId = element.find('input[name=session-id]').val();

                $.getJSON('./possibilities', {'session_id': sessionId}, function(data) {
                    var equipmentCodes = findIndexesThatMatch(data.equipment);
                    timeSlots.each(function() {
                        var timeSlot = $(this);
                        var equipmentCode = timeSlot.find('input[name=equipment-combo-code]').val();
                        var dateTimeId = parseInt(timeSlot.find('input[name=date-time-id]').val());

                        if (($.inArray(dateTimeId, data['date-times']) >= 0) || ($.inArray(equipmentCode, equipmentCodes) === -1)) {
                            timeSlot.droppable("option", "disabled", true);
                            timeSlot.css('background-color', '#f30');
                        }
                        else if (isBestChoice(equipmentCode, data.equipment)) {
                            timeSlot.css('background-color', '#ff0');
                        }
                        else {
                            timeSlot.css('background-color', equipment[equipmentCode].css);
                        }
                    });
                    disableTableWithLoading(false);
                });
            },
            stop: function() {
                var element = $(this);
                element.show();

                timeSlots.each(function() {
                    var timeSlot = $(this);
                    timeSlot.droppable("option", "disabled", false);
                    timeSlot.css('background-color', 'transparent');
                });
            }
        });
    });

    $('.time-slot').each(function() {
        $(this).droppable({
            drop: function(event, ui) {
                disableTableWithLoading(true);

                var element = $(this);
                var roomId = element.find('input[name=room-id]').val();
                var dateTimeId = element.find('input[name=date-time-id]').val();
                var sessionId = $(ui.draggable).find('input[name=session-id]').val();

                var plan = confirm(ui.draggable.text().trim() + ': \r\n' + element.find('input[name=plan-message]').val().trim());

                if (plan) {
                    $.getJSON(
                        './planSession',
                        {   'session_id':   sessionId,
                            'room_id':      roomId,
                            'date_time_id': dateTimeId},
                        function(data) {
                            if (data.success) {
                                $('#sessions-unscheduled').prepend(element.find('.session-block'));
                                element.prepend(ui.draggable);
                            }
                        }
                    );
                }

                disableTableWithLoading(false);
            }
        });
    });

    $('#sessions-unscheduled').droppable({
        drop: function(event, ui) {
            disableTableWithLoading(true);
            var element = $(this);
            var sessionId = $(ui.draggable).find('input[name=session-id]').val();

            $.getJSON(
                './returnSession', {'session_id': sessionId}, function(data) {
                    if (data.success) {
                        element.prepend(ui.draggable);
                    }
                    disableTableWithLoading(false);
                }
            );
        }
    });

    $('.session-block').mouseenter(function() {
        var element = $(this);
        noShow = false;

        setTimeout(function() {
            if (!noShow) {
                sessionInfo.hide();
                roomInfo.hide();

                var sessionId = element.find('input[name=session-id]').val();
                $.getJSON('./sessionInfo', {'session_id': sessionId}, function(data) {
                    if (data.success) {
                        sessionInfo.find('#code-label').next().text(data.code);
                        sessionInfo.find('#name-label').next().text(data.name);
                        sessionInfo.find('#commnent-label').next().text(data.comment);
                        sessionInfo.find('#participants-label').next().html('<li>'+data.participants.join('</li><li>')+'</li>');
                        sessionInfo.find('#equipment-label').next().html('<li>'+data.equipment.join('</li><li>')+'</li>');
                    }
                    else {
                        sessionInfo.find('#code-label').next().text(data.message);
                    }

                    var position = element.position();
                    var contentWidth = $('#content').outerWidth();
                    var contentHeight = $('#content').outerHeight();
                    var infoElementWidth = sessionInfo.outerWidth();
                    var infoElementHeight = sessionInfo.outerHeight();

                    var top = position.top + element.outerHeight() - 5;
                    var left = position.left;

                    if ((left + infoElementWidth) > contentWidth) {
                        left = left - (infoElementWidth - (contentWidth - left));
                    }

                    if ((top + infoElementHeight) > contentHeight) {
                        top = top - (infoElementHeight - (contentHeight - top));
                    }

                    sessionInfo.css({
                        top:    top,
                        left:   left
                    });
                    sessionInfo.show();
                });
            }
        }, 500);
    });

    $('.session-block').mouseleave(function() {
        noShow = true;
        sessionInfo.hide();
    });

    $('.room-indicator').mouseenter(function() {
        var element = $(this);
        noShow = false;

        setTimeout(function() {
            if (!noShow) {
                sessionInfo.hide();
                roomInfo.hide();

                var roomId = element.find('input[name=room-id]').val();
                $.getJSON('./roomInfo', {'room_id': roomId}, function(data) {
                    if (data.success) {
                        roomInfo.find('#roomnumnber-label').next().text(data.number);
                        roomInfo.find('#roomname-label').next().text(data.name);
                        roomInfo.find('#noofseats-label').next().text(data.seats);
                        roomInfo.find('#roomcomment-label').next().text(data.comment);
                    }
                    else {
                        roomInfo.find('#code-label').next().text(data.message);
                    }

                    var position = element.position();
                    var contentHeight = $('#content').outerHeight();
                    var infoElementHeight = roomInfo.outerHeight();

                    var top = position.top + 5;
                    var left = position.left + 20;

                    if ((top + infoElementHeight) > contentHeight) {
                        top = top - (infoElementHeight - (contentHeight - top));
                    }

                    roomInfo.css({
                        top:    top,
                        left:   left
                    });
                    roomInfo.show();
                });
            }
        }, 500);
    });

    $('.room-indicator').mouseleave(function() {
        noShow = true;
        roomInfo.hide();
    });
});