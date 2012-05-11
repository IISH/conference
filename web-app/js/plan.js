var noShow = true;
var equipment = new Object();
var timeSlots;
var colors = [
    [255, 94, 0], [255, 125, 0], [255, 156, 0], [255, 187, 0], [255, 219, 0],
    [255, 250, 0], [198, 255, 0], [167, 255, 0], [135, 255, 0], [104, 255, 0], [73, 255, 0], [42, 255, 0],
    [10, 255, 0], [0, 255, 21], [0, 255, 52], [0, 255, 83], [0, 255, 114], [0, 255, 146], [0, 255, 177], [0, 255, 208],
    [0, 255, 239], [0, 239, 255], [0, 208, 255], [0, 177, 255], [0, 146, 255], [0, 114, 255], [0, 83, 255], [0, 52, 255],
    [0, 21, 255], [10, 0, 255], [42, 0, 255], [73, 0, 255], [104, 0, 255], [135, 0, 255], [167, 0, 255], [198, 0, 255],
    [229, 0, 255], [255, 0, 250], [255, 0, 219], [255, 0, 187], [255, 0, 156], [255, 0, 125], [255, 0, 94], [255, 0, 62],
    [255, 0, 31], [255, 0, 255]
];

var findIndexesThatMatch = function(equipmentIds) {
    var ids = [];

    for (var equip in equipment) {
        var addIndex = true;
        for (var y=0; y<equipmentIds.length; y++) {
            if ($.inArray(equipmentIds[y], equip.ids) < 0) {
                addIndex = false;
            }
        }

        if (addIndex) {
            ids.push(parseInt(equip));
        }
    }

    return ids;
}

var isBestChoice = function(equipmentComboId, equipmentIds) {
    var bestChoice = true;

    for (var y=0; y<equipmentIds.length; y++) {
        var found = false;
        for (var x=0; x<equipment[equipmentComboId].ids.length; x++) {
            if (equipment[equipmentComboId].ids[x] === equipmentIds[y]) {
                found = true;
            }
        }

        if (!found) {
            bestChoice = false;
        }
    }

    return bestChoice;
}

var disableTableWithLoading = function(enable) {
    var overlay = $('#loading');
    var schedule = $('#schedule');

    if (enable) {
        var position = schedule.position();

        overlay.css({
            top:        position.top,
            left:       position.left,
            width:      schedule.width()+1,
            height:     schedule.height()+1
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
    var colorsCountSkip = parseInt(colors.length/equipmentCombos.length);

    equipment["-1"] = new Object();
    equipment["-1"].ids = [];
    equipment["-1"].css = "#ccc";

    equipmentCombos.each(function() {
        var element = $(this);

        var index = element.find('input[name=equipment-combo-id]').val();
        equipment[index] = new Object();
        equipment[index].ids = element.find('input[name=equipment-ids]').val().split(',');

        for (var i=0; i<equipment[index].ids.length; i++) {
            equipment[index].ids[i] = parseInt(equipment[index].ids[i]);
        }

        equipment[index].css = 'rgb('+colors[colorsCount][0]+' ,'+colors[colorsCount][1]+' , '+colors[colorsCount][2]+')';
        element.css('background-color', equipment[index].css);
        colorsCount = colorsCount + colorsCountSkip;
    });

    timeSlots = $('#schedule td.time-slot');

    $('.session-block').each(function() {
        $(this).draggable({
            revert: 'invalid',
            helper: 'clone',
            start: function() {
                disableTableWithLoading(true);

                var element = $(this);
                element.hide();

                var sessionId = element.find('input[name=session-id]').val();

                $.getJSON('./possibilities', {'session_id': sessionId}, function(data) {
                    var equipmentIds = findIndexesThatMatch(data.equipment);
                    timeSlots.each(function() {
                        var timeSlot = $(this);
                        var equipmentId = parseInt(timeSlot.find('input[name=equipment-combo-id]').val());
                        var dateTimeId = parseInt(timeSlot.find('input[name=date-time-id]').val());

                        if (($.inArray(dateTimeId, data['date-times']) >= 0) || ($.inArray(equipmentId, equipmentIds) === -1)) {
                            timeSlot.droppable("option", "disabled", true);
                            timeSlot.css('background-color', '#f30');
                        }
                        else if (isBestChoice(equipmentId, equipmentIds)) {
                            timeSlot.css('background-color', '#ff0');
                        }
                        else {
                            timeSlot.css('background-color', equipment[equipmentId].css);
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
                        disableTableWithLoading(false);
                    }
                );
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
                var sessionId = element.find('input[name=session-id]').val();
                $.getJSON('./sessionInfo', {'session_id': sessionId}, function(data) {
                    var infoElement =  $('#session-info');

                    if (data.success) {
                        infoElement.find('#code-label').next().text(data.code);
                        infoElement.find('#name-label').next().text(data.name);
                        infoElement.find('#commnent-label').next().text(data.comment);
                        infoElement.find('#participants-label').next().text(data.participants);
                        infoElement.find('#equipment-label').next().text(data.equipment);
                    }
                    else {
                        infoElement.find('#code-label').next().text(data.message);
                    }

                    var position = element.position();
                    infoElement.css({
                        top:    position.top + element.height() + 2,
                        left:   position.left + element.width() + 2
                    });
                    infoElement.show();
                });
            }
        }, 500);
    });

    $('.session-indicator').mouseleave(function() {
        noShow = true;
        $('#session-info').hide();
    });

    $('.room-indicator').mouseenter(function() {
        var element = $(this);
        noShow = false;

        setTimeout(function() {
            if (!noShow) {
                var roomId = element.find('input[name=room-id]').val();
                $.getJSON('./roomInfo', {'room_id': roomId}, function(data) {
                    var infoElement = $('#room-info');

                    if (data.success) {
                        infoElement.find('#roomnumnber-label').next().text(data.number);
                        infoElement.find('#roomname-label').next().text(data.name);
                        infoElement.find('#noofseats-label').next().text(data.seats);
                        infoElement.find('#roomcomment-label').next().text(data.comment);
                    }
                    else {
                        infoElement.find('#code-label').next().text(data.message);
                    }

                    var position = element.position();
                    infoElement.css({
                        top:    position.top + element.height() + 2,
                        left:   position.left + element.width() + 2
                    });
                    infoElement.show();
                });
            }
        }, 500);
    });

    $('.room-indicator').mouseleave(function() {
        noShow = true;
        $('#room-info').hide();
    });
});