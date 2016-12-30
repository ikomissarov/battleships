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
                case "OVER":
                    $(cell).addClass('board-hit');
                    showMessage("You have sunk enemy's ship. You have won the battle!");
                    $('#hisBoard').find('.board-cell').off('click');
                    break;
                case "KILL":
                    $(cell).addClass('board-hit').off('click');
                    showMessage("You have sunk enemy's ship. Waiting for enemy's turn.");
                    subscribe();
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

    $('#readyBtn').click(function () {
        var coords = new Array(11);
        for (var i = 0; i < coords.length; i++) {
            coords[i] = new Array(11);
        }
        $('#myBoard').find('.board-ship').each(function (i, cell) {
            var col = $(cell).index();
            var row = $(cell).closest('tr').index();
            coords[row][col] = true;
        });

        var ships = buildShips(coords);
        console.log(ships);

        var errorText;
        if (errorText = validate(ships)) {
            showErrorMessage(errorText);
            return;
        }

        showMessage('OK!');

        $('#myBoard').find('.board-cell').off('click');
        $(this).parent().slideUp('slow');

        $.post({
            url: 'game/ready',
            data: JSON.stringify({'ships': ships}),
            contentType: "application/json;charset=utf-8"
        }).done(function (data) {
            switch (data.type) {
                case "READY":
                    showMessage("Enemy is ready. Start the battle!");
                    blocked = false;
                    break;
                case "NOT_READY":
                    showMessage("Waiting for enemy's fleet to arrive.");
                    subscribe();
                    break;
                default:
                    showErrorMessage("Error: " + data.text);
            }
        });
    });
});

function buildShips(coords) {
    var ships = [];
    for (var i = 1; i < coords.length; i++)
        for (var j = 1; j < coords[i].length; j++) {
            var ship = [];
            findConnected(i, j);
            if (ship.length > 0) ships.push({'coords': ship});
        }

    return ships;

    function findConnected(i, j) {
        if (coords[i] && coords[i][j]) {
            ship.push({'row': i, 'col': j});
            coords[i][j] = false;
            findConnected(i, j - 1);
            findConnected(i, j + 1);
            findConnected(i - 1, j);
            findConnected(i + 1, j);
        }
    }
}

function validate(ships) {
    var count = [];
    for (var i = 0; i < ships.length; i++) {
        var shipSize = ships[i].coords.length;
        if (count[shipSize]) count[shipSize]++;
        else count[shipSize] = 1;
    }
    if (count.length > 5) {
        return 'Must NOT be ships bigger than 4-cell!';
    }
    if (count[4] !== 1) {
        return 'Must be ONE 4-cell ship present!';
    }
    if (count[3] !== 2) {
        return 'Must be TWO 3-cell ship present!';
    }
    if (count[2] !== 3) {
        return 'Must be THREE 2-cell ship present!';
    }
    if (count[1] !== 4) {
        return 'Must be FOUR 1-cell ship present!';
    }
}

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
                case "KILL":
                case "HIT":
                case "MISS":
                    subscribe.errorCount = 0;
                    showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. Your turn.");
                    onFire(data.coords.row, data.coords.col);
                    blocked = false;
                    break;
                case "OVER":
                    showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. You have lost the battle!");
                    onFire(data.coords.row, data.coords.col);
                    $('#hisBoard').find('.board-cell').off('click');
                    break;
                case "EMPTY":
                    subscribe.errorCount = 0;
                    subscribe();
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
    $('#status').html(message);
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
