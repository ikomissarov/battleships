$(document).ready(function () {

    $(document).ajaxSuccess(function (event, xhr, settings, data) {
        console.log(settings.url, ":", data);
    });

    $(document).ajaxError(function (event, xhr, settings) {
        console.error(settings.url, ":", xhr);
    });

    var LETTERS = ['', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K'];
    var blocked = true;

    var myBoard = $('#myBoard');
    var hisBoard = $('#hisBoard');

    fillTable(myBoard);
    fillTable(hisBoard);

    $('.enemyName').text(window.localStorage.getItem('enemyName'));

    $('#readyBtn').click(onReadyToStart);

    $(myBoard).on('click.gameTurn', '.board-cell', onMyBoardClick);
    $(hisBoard).on('click.gameTurn', '.board-cell:not(.board-hit):not(.board-miss)', onHisBoardClick);

    subscribe.errorCount = 0;
    subscribe.onError = function () {
        if (++subscribe.errorCount <= 10) {
            setTimeout(subscribe, 1000);
        } else {
            showMessage("Connection to the game is lost!", 'alert-danger');
        }
    };

    function onReadyToStart() {
        var coords = new Array(11);
        for (var i = 0; i < coords.length; i++) {
            coords[i] = new Array(11);
        }
        $(myBoard).find('.board-ship').each(function (i, cell) {
            var col = $(cell).index();
            var row = $(cell).closest('tr').index();
            coords[row][col] = true;
        });

        var ships = buildShips(coords);
        console.log(ships);

        var errorText;
        if (errorText = validate(ships, coords)) {
            showMessage(errorText, 'alert-danger');
            return;
        }

        showMessage('OK!', 'alert-success');

        $(myBoard).off('click.gameTurn');
        $(this).parent().slideUp('slow');

        $.post({
            url: 'game/ready',
            data: JSON.stringify({'ships': ships}),
            contentType: "application/json;charset=utf-8"
        }).done(function (data) {
            switch (data.type) {
                case "READY":
                    showMessage("Enemy is ready. Start the battle!", 'alert-info');
                    blocked = false;
                    break;
                case "NOT_READY":
                    showMessage("Waiting for enemy's fleet to arrive.", 'alert-warning');
                    subscribe();
                    break;
                default:
                    showMessage("Error: " + data.text, 'alert-danger');
            }
        });
    }

    function onMyBoardClick() {
        $(this).toggleClass('board-ship');
    }

    function onHisBoardClick() {
        if (blocked) return;

        //double click on cell to fire to avoid accidental fire
        if (!$(this).hasClass('board-fire')) {
            $(hisBoard).find('.board-fire').removeClass('board-fire');
            $(this).addClass('board-fire');
            return;
        }

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
            $(cell).removeClass('board-fire');
            switch (data.type) {
                case "OVER":
                    showMessage("You have sunk enemy's ship. You have won the battle!", 'alert-success');
                    onGameEnd(hisBoard, row, col);
                    break;
                case "KILL":
                    showMessage("You have sunk enemy's ship. Your turn.", 'alert-info');
                    onKill(hisBoard, row, col);
                    blocked = false;
                    break;
                case "HIT":
                    showMessage("You have hit enemy's ship. Your turn.", 'alert-info');
                    onHit(hisBoard, row, col);
                    blocked = false;
                    break;
                case "MISS":
                    showMessage("You have missed. Waiting for enemy's turn.", 'alert-warning');
                    onMiss(hisBoard, row, col);
                    subscribe();
                    break;
                default:
                    showMessage("Error: " + data.text, 'alert-danger');
            }
        }).fail(function (xhr, status) {
            showMessage("Error: " + status, 'alert-danger');
        });
    }

    function subscribe() {
        $.getJSON('game/subscribe')
            .done(function (data) {
                switch (data.type) {
                    case "OVER":
                        showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. You have lost the battle!", 'alert-danger');
                        onGameEnd(myBoard, data.coords.row, data.coords.col);
                        break;
                    case "KILL":
                        showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. Waiting for enemy's turn.", 'alert-warning');
                        onKill(myBoard, data.coords.row, data.coords.col);
                        subscribe.errorCount = 0;
                        subscribe();
                        break;
                    case "HIT":
                        showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. Waiting for enemy's turn.", 'alert-warning');
                        onHit(myBoard, data.coords.row, data.coords.col);
                        subscribe.errorCount = 0;
                        subscribe();
                        break;
                    case "MISS":
                        showMessage("Enemy has fired to <b>" + data.coords.row + LETTERS[data.coords.col] + "</b>. Your turn.", 'alert-info');
                        onMiss(myBoard, data.coords.row, data.coords.col);
                        subscribe.errorCount = 0;
                        blocked = false;
                        break;
                    case "EMPTY":
                        subscribe.errorCount = 0;
                        subscribe();
                        break;
                    case "QUIT":
                        showMessage("Enemy has left the battlefield!", 'alert-danger');
                        break;
                    case "REDIRECT":
                        window.open(data.text, "_self");
                        break;
                    default:
                        showMessage("Error: " + data.text, 'alert-danger');
                        subscribe.onError();
                }
            }).fail(subscribe.onError);
    }

    function onMiss(board, row, col) {
        $(findCell(board, row, col)).addClass('board-miss');
    }

    function onHit(board, row, col) {
        $(findCell(board, row, col)).addClass('board-hit');
    }

    function onKill(board, row, col) {
        onHit(board, row, col);
        markSunkShip(board, row, col);
        markCellsAroundSunkShip(board, row, col);
    }

    function onGameEnd(board, row, col) {
        onKill(board, row, col);
        $(hisBoard).off('click.gameTurn');
    }

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

    function validate(ships, coords) {
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

        return validateNoCornersTouch(ships, coords);
    }

    function validateNoCornersTouch(ships, coords) {
        for (var i = 0; i < ships.length; i++) {
            var ship = ships[i];
            for (var j = 0; j < ship.coords.length; j++) {
                var row = ship.coords[j].row;
                var col = ship.coords[j].col;
                if (notOk(row - 1, col - 1) ||
                    notOk(row - 1, col + 1) ||
                    notOk(row + 1, col - 1) ||
                    notOk(row + 1, col + 1)) {
                    return 'Ships must NOT touch each other corners!';
                }
            }

            function notOk(row, col) {
                //no such cell or not a ship cell, ok
                if (!validCoords(row, col) || coords[row][col] === undefined) return false;

                for (var k = 0; k < ship.coords.length; k++) {
                    //the same ship cell, ok
                    if (ship.coords[k].row === row && ship.coords[k].col === col) return false;
                }
                //not the same ship cell, not ok, touching another ship
                return true;
            }
        }
    }

    function markSunkShip(board, row, col) {
        if (!validCoords(row, col)) return;

        var cell = findCell(board, row, col);
        if (cell.hasClass('board-hit') && !cell.hasClass('board-kill')) {
            cell.addClass('board-kill');
            markSunkShip(board, row, col - 1);
            markSunkShip(board, row, col + 1);
            markSunkShip(board, row - 1, col);
            markSunkShip(board, row + 1, col);
        }
    }

    function markCellsAroundSunkShip(board, row, col) {
        if (!validCoords(row, col)) return;

        var cell = findCell(board, row, col);

        if (cell.hasClass('board-around-kill')) return;

        cell.addClass('board-around-kill');

        if (cell.hasClass('board-kill')) {
            markCellsAroundSunkShip(board, row - 1, col - 1);
            markCellsAroundSunkShip(board, row - 1, col);
            markCellsAroundSunkShip(board, row - 1, col + 1);
            markCellsAroundSunkShip(board, row, col + 1);
            markCellsAroundSunkShip(board, row + 1, col + 1);
            markCellsAroundSunkShip(board, row + 1, col);
            markCellsAroundSunkShip(board, row + 1, col - 1);
            markCellsAroundSunkShip(board, row, col - 1);
        }
    }

    function findCell(board, row, col) {
        return $(board).find('tr').eq(row).find('td').eq(col);
    }

    function validCoords(row, col) {
        return row > 0 && row < 11 && col > 0 && col < 11;
    }

    function showMessage(message, className) {
        $('#status').html(message);
        $('#statusBar').removeClass('alert-info alert-warning alert-success alert-danger').addClass(className);
    }

    function fillTable(table) {
        var row, cell;
        for (var i = 0; i < 11; i++) {
            row = $('<tr></tr>');

            if (i) {
                row.addClass('board-row');
                for (var j = 0; j < 11; j++) {
                    cell = $('<td></td>');
                    if (j) cell.addClass('board-cell');
                    else cell.text(i); //this is vertical header
                    row.append(cell);
                }
            } else { //this is horizontal header
                for (var k = 0; k < 11; k++) {
                    cell = $('<td></td>').text(LETTERS[k]);
                    row.append(cell);
                }
            }

            table.append(row);
        }
    }
});
