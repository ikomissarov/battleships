var LETTERS = [, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K'];

$(document).ready(function () {
    $('#showLoading').click(function () {
        $('#loading').show();
        setTimeout(function () {
            $('#loading').hide();
        }, 25000);
    });

    /*$(document).ajaxSend(function () {
     $('#loading').show();
     });
     $(document).ajaxComplete(function () {
     $('#loading').hide();
     });*/

    subscribe.errorCount = 0;
    subscribe.onError = function () {
        if (++subscribe.errorCount <= 10) {
            setTimeout(subscribe, 1000);
        } else {
            showErrorMessage("Connection to the game is lost!");
        }
    };

    fillTable($('#myBoard'));
    fillTable($('#hisBoard'));

    $('#alert').click(function () {
        $(this).hide();
    });

    $('#myBoard').find('.board-cell').click(function () {
        $(this).toggleClass('board-ship');
    });

    $('#readyBtn').click(function () {
        $('#myBoard').find('.board-cell').off('click');
        $(this).parent().slideUp('slow');

        var fleet = {'ships': []};
        $('#myBoard').find('.board-ship').each(function (i, cell) {
            var col = $(cell).index();
            var row = $(cell).closest('tr').index();
            fleet.ships.push([row, col]);
        });
        console.log(fleet);

        $.post('game/ready', fleet).done(function (data) {
            $('#hisBoard').find('.board-cell').click(function () {
                var cell = this;
                var col = $(cell).index();
                var row = $(cell).closest('tr').index();

                var coords = {'row': row, 'col': col};
                console.log(coords);

                $.post('game/fire', coords)
                    .done(function (data) {
                        switch (data.type) {
                            case "VICTORY":
                                $(cell).addClass('board-hit');
                                showMessage("You have hit enemy's ship. You have won the battle!");
                                $('#hisBoard').find('.board-cell').off('click');
                                break;
                            case "HIT":
                                $(cell).addClass('board-hit').off('click');
                                showMessage("You have hit enemy's ship. Waiting for enemy's turn.");
                                subscribe();
                                break;
                            case "MISS":
                                $(cell).addClass('board-miss').off('click');
                                showMessage("You have missed. Waiting for enemy's turn.");
                                subscribe();
                                break;
                            default:
                                showErrorMessage("Error: " + data.text);
                        }
                    })
                    .fail(function (xhr, status) {
                        showErrorMessage("Error: " + status);
                    });
            });

            if (data.enemyReady) {
                showMessage("Waiting for enemy's turn.");
            } else {
                showMessage("Waiting for enemy's fleet to arrive.");
            }

            subscribe();
        });
    });
});

function onFire(row, col) {
    var cell = $('#myBoard').find('tr').eq(row).find('td').eq(col);
    if ($(cell).hasClass('board-ship')) {
        $(cell).addClass('board-hit');
        //showMessage('Enemy has hit your ship.');
    } else {
        $(cell).addClass('board-miss');
        //showMessage('Enemy has missed.');
    }
}

function subscribe() {
    $('#loading').show();
    $.getJSON('game/subscribe')
        .done(function (data) {
            $('#loading').hide();
            switch (data.type) {
                case "READY":
                    subscribe.errorCount = 0;
                    showMessage("Enemy's fleet has arrived. Start the battle!");
                    break;
                case "FIRE":
                    subscribe.errorCount = 0;
                    showMessage("Enemy has fired to " + data.coords + ". Your turn.");
                    onFire(data.coords[0], data.coords[1]);
                    break;
                case "EMPTY":
                    subscribe.errorCount = 0;
                    subscribe();
                    break;
                case "DEFEAT":
                    showMessage("Enemy has fired to " + data.coords + ". You have lost the battle!");
                    onFire(data.coords[0], data.coords[1]);
                    $('#hisBoard').find('.board-cell').off('click');
                    break;
                case "QUIT":
                    showErrorMessage("Enemy has left the battlefield!");
                    break;
                case "REDIRECT":
                    window.open(data.text, "_self");
                    break;
                default:
                    showErrorMessage("Error: " + data.text);
                    subscribe.onError();
            }
        })
        .fail(subscribe.onError);
}

function showMessage(message) {
    console.info(message);
    $('#status').html(message);
}

function showErrorMessage(message) {
    console.error(message);
}

function fillTable(table) {
    var row, cell;
    for (var i = 0; i < 11; i++) {
        row = $('<tr></tr>');
        if (i) row.addClass('board-row');
        for (var j = 0; j < 11; j++) {
            cell = $('<td></td>');
            if (i) {
                if (j) {
                    cell.addClass('board-cell');
                } else {
                    cell.text(i);
                }
            } else {
                if (j) {
                    cell.text(LETTERS[j]);
                }
            }
            row.append(cell);
        }
        table.append(row);
    }
}
