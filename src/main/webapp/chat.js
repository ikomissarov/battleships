$(document).ready(function () {

    var myMsgProtoElem = $('.chat .message:nth-child(1)');
    var hisMsgProtoElem = $('.chat .message:nth-child(2)');

    $('#open-chat').click(function () {
        $('#chat').show('fast');
        return false;
    });

    $('#close-chat').click(function () {
        $('#chat').hide('fast');
    });

    $('#chat-send').click(function () {
        $('#chat-msg').val(function (i, message) {
            if (message) {
                sendMessage(message);
                return '';
            }
            return message;
        });
    });

    $('#chat-msg').keypress(function (event) {
        if (event.which === 13) { //send message on Enter pressed
            $('#chat-send').click();
        }
    });

    subscribe.errorCount = 0;
    subscribe.onError = function () {
        if (++subscribe.errorCount <= 10) {
            setTimeout(subscribe, 1000);
        } else {
            showError("Disconnected from chat.");
        }
    };
    subscribe();

    function subscribe() {
        $.getJSON('chat/subscribe')
            .done(function (data) {
                switch (data.type) {
                    case "MSG":
                        subscribe.errorCount = 0;
                        showMessage(data.userName, data.text, hisMsgProtoElem);
                        subscribe();
                        break;
                    case "NO_MSG":
                        subscribe.errorCount = 0;
                        subscribe();
                        break;
                    case "QUIT":
                        showError(data.userName + " left the chat.");
                        break;
                    case "REDIRECT":
                        window.open(data.text, "_self");
                        break;
                    default:
                        showError("<b>Error:</b> " + data.text);
                        subscribe.onError();
                }
            })
            .fail(subscribe.onError);
    }

    function sendMessage(message) {
        $.post({
            url: 'chat/publish',
            data: message,
            contentType: "text/plain;charset=utf-8"
        }).done(function (data) {
            switch (data.type) {
                case "MSG":
                    showMessage(data.userName, data.text, myMsgProtoElem);
                    break;
                case "REDIRECT":
                    window.open(data.text, "_self");
                    break;
                default:
                    showError("<b>Error:</b> " + data.text);
            }
        });
    }

    function showMessage(nickname, message, protoElem) {
        var newElem = protoElem.clone();
        newElem.find('.nickname').text(nickname);
        newElem.find('.text').text(message);
        newElem.find('.time').append(timeNow());
        $('#chat-body').append(newElem);
    }

    function showError(message) {
        var errorElem = $('<li></li>').html(message).addClass('error');
        $('#chat-body').append(errorElem);
    }

    function timeNow() {
        var d = new Date();
        var h = (d.getHours() < 10 ? '0' : '') + d.getHours();
        var m = (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
        return h + ':' + m;
    }
});