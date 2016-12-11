var errorCount = 0;

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

window.onunload = function () {
    var xhr = new XMLHttpRequest();
    //false param means that request is NOT async as usually so browser will not close before sending it
    xhr.open("POST", 'publish', false);
    xhr.send("POISON_MSG");
};

subscribe();

function subscribe() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState != 4) return;

        console.log(this);
        if (this.status == 200) {
            errorCount = 0;
            var msg = JSON.parse(this.responseText);
            switch (msg.type) {
                case "MSG":
                    showMessage("<b>" + msg.userName + ":</b> " + msg.text, "green");
                    subscribe();
                    break;
                case "NO_MSG":
                    console.log("No new messages.");
                    subscribe();
                    break;
                case "QUIT":
                    showMessage(msg.userName + " left the chat.", "red");
                    break;
                case "REDIRECT":
                    window.open(msg.text, "_self");
                    break;
                default:
                    showMessage("<b>Error:</b> " + msg.text, "red");
            }
            return;
        }

        showMessage("<b>Error:</b> " + this.status + " - " + this.statusText, "red");

        if (++errorCount <= 10) {
            setTimeout(subscribe, 1000); // попробовать ещё раз через 1 сек
        } else {
            showMessage("Disconnected from chat.", "red");
        }
    };
    xhr.open("GET", 'subscribe', true);
    xhr.send();
}

function sendMessage(message) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState != 4) return;

        console.log(this);
        if (this.status == 200) {
            var msg = JSON.parse(this.responseText);
            switch (msg.type) {
                case "MSG":
                    showMessage("<b>" + msg.userName + ":</b> " + msg.text, "blue");
                    break;
                case "REDIRECT":
                    window.open(msg.text, "_self");
                    break;
                default:
                    showMessage("<b>Error:</b> " + msg.text, "red");
            }
            return;
        }

        showMessage("<b>Error:</b> " + this.status + " - " + this.statusText, "red");
    };
    xhr.open("POST", 'publish', true);
    xhr.send(message);
}

function showMessage(message, className) {
    var messageElem = document.createElement('div');
    messageElem.className = className;
    messageElem.innerHTML = message;
    chat.appendChild(messageElem);
}
