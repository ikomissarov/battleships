var form = document.forms.publish;
var chat = document.getElementById('subscribe');

form.onsubmit = function () {
    var message = form.message.value;
    if (message) {
        form.message.value = '';
        sendMessage(message);
    }
    return false;
};

subscribe();

function subscribe() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState != 4) return;

        console.log(this);
        if (this.status == 200) {
            showMessage(this.responseText);
            subscribe();
            return;
        }

        if (this.status != 404) { // 404 может означать, что сервер перезагружается
            showMessage(this.statusText); // показать ошибку
        }

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
    showMessage(message);
}

function showMessage(message) {
    var messageElem = document.createElement('div');
    messageElem.appendChild(document.createTextNode(message));
    chat.appendChild(messageElem);
}