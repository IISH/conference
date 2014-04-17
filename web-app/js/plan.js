var timeOut = null;
var curSessionId = null;
var curSessionBlock = null;
var curSessionBlockParent = null;
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

    if (enable) {
        var schedule = $('#schedule');
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

var unselectSession = function() {
    curSessionBlock.removeClass('selected');
            
    curSessionId = null;
    curSessionBlock = null;
    curSessionBlockParent = null;

    timeSlots.css('background-color', 'transparent');
    timeSlots.removeClass('click-to-plan');
    
    $('#unschedule-session').hide();
    
    disableTableWithLoading(false);
}

var unscheduleSessionBlock = function() {
    var sessionCode = curSessionBlock.text().trim();
    
    $('#sessions-unscheduled .session-block').each(function(i, block) {
        block = $(block);
        
        if (block.text().trim() > sessionCode) {
            block.before(curSessionBlock);
            return false;
        }
    });

    $('#sessions-unscheduled').append(curSessionBlock);
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

    ajaxCall(this, 'session/conflicts', {}, function(data) {
        if ((data.noShow.length === 0) && (data.alreadyPlanned.length === 0) && (data.equipmentProblems.length === 0)) {
            $('#conflicts-searching').hide();
            $('#no-conflicts').show();
        }
        else {
            var conflictsBox = $('#conflicts ul');

            for (var i=0; i<data.noShow.length; i++) {
                var noShowText =
                    '<li>- ' +
                    '<a href="' + data.noShow[i].userUrl + '">' +
                    data.noShow[i].plannedUser +
                    '</a> ' +
                    data.noShow[i].text;
                    
                for (var j=0; j<data.noShow[i].conflictingSessions.length; j++) {
                   noShowText += 
                        ' <a href="' + data.noShow[i].conflictingSessionsUrls[j] + '">' +
                        data.noShow[i].conflictingSessions[j] +
                        '</a>,';
                }
                
                noShowText = noShowText.substring(0, noShowText.length-1);
                noShowText += '</li>';         
                
                conflictsBox.prepend(noShowText);
            }
            
            for (var i=0; i<data.alreadyPlanned.length; i++) {
                var alreadyPlannedText =
                    '<li>- ' +
                    '<a href="' + data.alreadyPlanned[i].userUrl + '">' +
                    data.alreadyPlanned[i].plannedUser +
                    '</a> ' +
                    data.alreadyPlanned[i].text;
                    
                for (var j=0; j<data.alreadyPlanned[i].conflictingSessions.length; j++) {
                   alreadyPlannedText += 
                        ' <a href="' + data.alreadyPlanned[i].conflictingSessionsUrls[j] + '">' +
                        data.alreadyPlanned[i].conflictingSessions[j] +
                        '</a>,';
                }
                
                alreadyPlannedText = alreadyPlannedText.substring(0, alreadyPlannedText.length-1);
                alreadyPlannedText += '</li>';         
                
                conflictsBox.prepend(alreadyPlannedText);
            }

            for (var i=0; i<data.equipmentProblems.length; i++) {
                conflictsBox.prepend('<li>- ' +
                    '<a href="' + data.equipmentProblems[i].sessionUrl + '">' +
                    data.equipmentProblems[i].plannedSession +
                    '</a> ' +
                    data.equipmentProblems[i].text +
                    ' <a href="' + data.equipmentProblems[i].roomUrl + '">' +
                    data.equipmentProblems[i].plannedRoom +
                    '</a>' +
                    '</li>');
            }

            $('#conflicts-searching').hide();
            $('#conflicts').show();
        }
    }, 
    function() {
        $('#conflicts-searching').hide();
    });

    $(document).keydown(function(e) {
        if (e.keyCode === 27 && curSessionBlock !== null) {
            unselectSession();
        }
    });

    $('.session-block').click(function(e) {
        e.stopPropagation();
        disableTableWithLoading(true);
        
        var element = $(this);        
        if (element.hasClass('selected')) {
            element.removeClass('selected');
            unselectSession();
        }
        else {
            if (curSessionBlock !== null) {
                curSessionBlock.removeClass('selected');
            }

            curSessionBlock = element;
            curSessionBlock.addClass('selected');
            curSessionBlockParent = curSessionBlock.parent();
            curSessionId = parseInt(curSessionBlock.find('input').val());

            ajaxCall(this, 'session/possibilities', {'session_id': curSessionId}, function(data) {
                var equipmentCodes = findIndexesThatMatch(data.equipment);
                timeSlots.each(function() {
                    var timeSlot = $(this);
                    var equipmentCode = timeSlot.find('input[name=equipment-combo-code]').val();
                    var dateTimeId = parseInt(timeSlot.find('input[name=date-time-id]').val());

                    if (($.inArray(dateTimeId, data['date-times']) >= 0) || ($.inArray(equipmentCode, equipmentCodes) === -1)) {
                        timeSlot.css('background-color', '#f30');
                        timeSlot.removeClass("click-to-plan");
                    }
                    else if (isBestChoice(equipmentCode, data.equipment)) {
                        timeSlot.css('background-color', '#ff0');
                        (curSessionBlockParent[0] === timeSlot[0]) ? timeSlot.removeClass("click-to-plan") : timeSlot.addClass("click-to-plan");
                    }
                    else {
                        timeSlot.css('background-color', equipment[equipmentCode].css);
                        (curSessionBlockParent[0] === timeSlot[0]) ? timeSlot.removeClass("click-to-plan") : timeSlot.addClass("click-to-plan");
                    }
                });
                
                var unschedule = $('#unschedule-session');
                var unscheduled = $('#sessions-unscheduled');
                                
                if (curSessionBlockParent[0] === unscheduled[0]) {
                    unschedule.hide();
                }
                else {
                    unschedule.css('display', 'table').css('height', unscheduled.css('height'));
                }
                
                 disableTableWithLoading(false);
            });
        }
    });

    $(document).on("click", '.time-slot.click-to-plan', function(e) {
        disableTableWithLoading(true);

        var element = $(this);
        var roomId = element.find('input[name=room-id]').val();
        var dateTimeId = element.find('input[name=date-time-id]').val();

        var contPlan = true;
        var plannedSession = element.find('.session-block');

        if (plannedSession.length === 1) {
            contPlan = confirm("A session is already planned on this time slot! \r\nDo you want to continue?");
        }

        if (contPlan) {
            ajaxCall(
                this, 'session/planSession',
                {   'session_id':   curSessionId,
                    'room_id':      roomId,
                    'date_time_id': dateTimeId},
                function(data) {
                    if (data.success) {
                        $('#sessions-unscheduled').prepend(plannedSession);
                        element.prepend(curSessionBlock);
                    }
                    unselectSession();
                }
            );
        }
    });

    $(document).on("click", '#unschedule-session', function(e) {
        disableTableWithLoading(true);
        
        ajaxCall(
            this, 'session/returnSession', {'session_id': curSessionId}, function(data) {
                if (data.success) {
                    unscheduleSessionBlock();
                }
                unselectSession();
            }
        );
    });
    
    $('.session-block').mouseenter(function() {
        var element = $(this);

        clearTimeout(timeOut);
        sessionInfo.hide();

        timeOut = setTimeout(function() {
            roomInfo.hide();

            var session_id = element.find('input[name=session-id]').val();
            ajaxCall(this, 'session/sessionInfo', {'session_id': session_id}, function(data) {
                if (data.success) {
                    (data.code !== null)            ? sessionInfo.find('#code-label').next().text(data.code)                                            : sessionInfo.find('#code-label').next().text('-');
                    (data.name !== null)            ? sessionInfo.find('#name-label').next().text(data.name)                                            : sessionInfo.find('#name-label').next().text('-');
                    (data.state !== null)           ? sessionInfo.find('#state-label').next().text(data.state)                                          : sessionInfo.find('#state-label').next().text('-');
                    (data.comment !== null)         ? sessionInfo.find('#commnent-label').next().text(data.comment)                                     : sessionInfo.find('#commnent-label').next().text('-');
                    (data.equipment.length > 0)     ? sessionInfo.find('#equipment-label').next().html('<li>'+data.equipment.join('</li><li>')+'</li>') : sessionInfo.find('#equipment-label').next().text('-');
                    
                    setParticipantDataForSession(data);
                    if (data.participants.length === 0) {
                        sessionInfo.find('#participants-label').next().prepend('<li>-</li>');
                    }
                }
                else {
                    sessionInfo.find('#code-label').next().text(data.message);
                }

                var position = element.position();
                var contentWidth = $('#content').outerWidth();
                var contentHeight = $('#content').outerHeight();
                var infoElementWidth = sessionInfo.outerWidth();
                var infoElementHeight = sessionInfo.outerHeight();

                var top = position.top + element.outerHeight();
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
        }, 500);
    });

    $('.session-block').mouseleave(function() {
        clearTimeout(timeOut);
        sessionInfo.hide();
    });

    $('.room-indicator').mouseenter(function() {
        var element = $(this);

        clearTimeout(timeOut);
        roomInfo.hide();

        timeOut = setTimeout(function() {
            sessionInfo.hide();

            var roomId = element.find('input[name=room-id]').val();
            ajaxCall(this, 'session/roomInfo', {'room_id': roomId}, function(data) {
                if (data.success) {
                    (data.number !== null)  ? roomInfo.find('#roomnumnber-label').next().text(data.number)  : roomInfo.find('#roomnumnber-label').next().text('-');
                    (data.name !== null)    ? roomInfo.find('#roomname-label').next().text(data.name)       : roomInfo.find('#roomname-label').next().text('-');
                    (data.seats !== null)   ? roomInfo.find('#noofseats-label').next().text(data.seats)     : roomInfo.find('#noofseats-label').next().text('-');
                    (data.comment !== null) ? roomInfo.find('#roomcomment-label').next().text(data.comment) : roomInfo.find('#roomcomment-label').next().text('-');
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
        }, 500);
    });

    $('.room-indicator').mouseleave(function() {
        clearTimeout(timeOut);
        roomInfo.hide();
    });
});