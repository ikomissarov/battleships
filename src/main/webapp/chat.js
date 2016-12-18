$(document).ready(function () {

    $(document).ajaxSuccess(function (event, xhr, settings, data) {
        console.log(settings.url, ":", data);
    });

    $(document).ajaxError(function (event, xhr, settings) {
        console.error(settings.url, ":", xhr);
        showMessage("<b>Error:</b> " + xhr.status + " - " + xhr.statusText, "red");
    });

    $('#publish').submit(function () {
        $('#msg').val(function (i, message) {
            if (message) {
                sendMessage(message);
                return '';
            }
            return message;
        });
        return false;
    });

    subscribe.errorCount = 0;
    subscribe.onError = function () {
        if (++subscribe.errorCount <= 10) {
            setTimeout(subscribe, 1000);
        } else {
            showMessage("Disconnected from chat.", "red");
        }
    };
    subscribe();
});

function subscribe() {
    $.getJSON('subscribe')
        .done(function (data) {
            switch (data.type) {
                case "MSG":
                    subscribe.errorCount = 0;
                    showMessage("<b>" + data.userName + ":</b> " + data.text, "green");
                    subscribe();
                    break;
                case "NO_MSG":
                    subscribe.errorCount = 0;
                    subscribe();
                    break;
                case "QUIT":
                    showMessage(data.userName + " left the chat.", "red");
                    break;
                case "REDIRECT":
                    window.open(data.text, "_self");
                    break;
                default:
                    showMessage("<b>Error:</b> " + data.text, "red");
                    subscribe.onError();
            }
        })
        .fail(subscribe.onError);
}

function sendMessage(message) {
    $.post('publish', message)
        .done(function (data) {
            switch (data.type) {
                case "MSG":
                    showMessage("<b>" + data.userName + ":</b> " + data.text, "blue");
                    break;
                case "REDIRECT":
                    window.open(data.text, "_self");
                    break;
                default:
                    showMessage("<b>Error:</b> " + data.text, "red");
            }
        });
}

function showMessage(message, className) {
    var messageElem = $('<div></div>').html(message).addClass(className);
    $('#subscribe').append(messageElem);
}