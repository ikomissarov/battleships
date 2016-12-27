var LETTERS = [, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K'];
var blocked = true;

$(document).ready(function () {

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
            fleet.ships.push({'row': row, 'col': col});
        });
        console.log(fleet);

        $.post({
            url: 'game/ready',
            data: JSON.stringify(fleet),
            contentType: "application/json;charset=utf-8"
        }).done(function (data) {
            $('#hisBoard').find('.board-cell').click(function () {
                if (blocked) return;

                blocked = true;
                var cell = this;
                var col = $(cell).index();
                var row = $(cell).closest('tr').index();

                var coords = {'row': row, 'col': col};
                console.log(coords);

                $.post({
                    url: 'game/fire',
                    data: JSON.stringify(coords),
                    contentType: "application/json;charset=utf-8"
                }).done(function (data) {
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
                showMessage("Enemy is ready. Start the battle!");
                blocked = false;
            } else {
                showMessage("Waiting for enemy's fleet to arrive.");
                subscribe();
            }
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
    $.getJSON('game/subscribe')
        .done(function (data) {
            switch (data.type) {
                case "FIRE":
                    subscribe.errorCount = 0;
                    showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. Your turn.");
                    onFire(data.coords.row, data.coords.col);
                    blocked = false;
                    break;
                case "EMPTY":
                    subscribe.errorCount = 0;
                    subscribe();
                    break;
                case "DEFEAT":
                    showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. You have lost the battle!");
                    onFire(data.coords.row, data.coords.col);
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
