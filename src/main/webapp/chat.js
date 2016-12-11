var POISON_MSG = "POISON_MSG";
var form = document.forms.publish;
var chat = document.getElementById('subscribe');

var myName = getCookie("myName");
var hisName = getCookie("hisName");

form.onsubmit = function () {
    var message = form.message.value;
    if (message) {
        form.message.value = '';
        sendMessage(message);
    }
    return false;
};

window.onunload = function () {
    var xhr = new XMLHttpRequest();
    //false param means that request is NOT async as usually so browser will not close before sending it
    xhr.open("POST", 'publish', false);
    xhr.send(POISON_MSG);
};

subscribe();

function subscribe() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState != 4) return;

        console.log(this);
        if (this.status == 200) {
            var msg = this.responseText;
            if (msg === POISON_MSG) {
                showMessage(hisName + " left the chat.", "red");
            } else {
                showMessage("<b>" + hisName + ":</b> " + msg, "green");
                subscribe();
            }
            return;
        }

        showMessage("<b>Error:</b> " + this.status + " - " + this.statusText, "red");

        setTimeout(subscribe, 1000); // попробовать ещё раз через 1 сек
    };
    xhr.open("GET", 'subscribe', true);
    xhr.send();
}

function sendMessage(message) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", 'publish', true);
    // просто отсылаю сообщение "как есть" без кодировки
    // если бы было много данных, то нужно было бы отослать JSON из объекта с ними
    // или закодировать их как-то иначе
    xhr.send(message);
    showMessage("<b>" + myName + ":</b> " + message, "blue");
}

function showMessage(message, className) {
    var messageElem = document.createElement('div');
    messageElem.className = className;
    messageElem.innerHTML = message;
    chat.appendChild(messageElem);
}

// возвращает cookie с именем name, если есть, если нет, то undefined
function getCookie(name) {
    var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}
