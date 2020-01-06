let webSocket;
const messages = document.getElementById("chatWindow");
const users = document.getElementById('chatUsers');
let user;
let msg = {
};

document.getElementById("input").onkeyup = function(event) {
    if (document.getElementById("input").value.length !== 0) {
        const writingMsg = {
            type: 'WRITING',
            message: '',
            date: '',
            user: user
        };
        console.log('WRITE');
        webSocket.send(JSON.stringify(writingMsg));
    } else {
        const writingMsg = {
            type: 'STOP_WRITING',
            message: '',
            date: '',
            user: user
        };
        console.log('STOP');
        webSocket.send(JSON.stringify(writingMsg));
    }
};

function openSocket() {
    user = document.getElementById('username').value;
    if (user.length === 0) {
        alert('Please provide a nickname!');
        return;
    }
    // Ensures only one connection is open at a time
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        if (document.getElementById('chatView').hasAttribute('style')) {
            sendConnectRequest();
        }
        return;
    }

    webSocket = new WebSocket("ws://localhost:8080/ChatServer_war_exploded/chat");
    /**
     * Binds functions to the listeners for the websocket.
     */
    webSocket.onmessage = function(event){
        const response = JSON.parse(event.data);
        switch (response.type) {
            case 'CONNECT':
                if (response.message !== '') {
                    user = response.message;
                    document.getElementById('username').value = user;
                    alert(`Username already taken! We propose ${response.message}`);
                } else {
                    document.getElementById('connectView').setAttribute('style', 'display: none');
                    document.getElementById('chatView').removeAttribute('style');
                }
                writeResponse(`${response.user} connected!`);
                break;
            case 'DISCONNECT':
                writeResponse(`${response.user} disconnected!`);
                break;
            case 'MESSAGE':
                writeResponse(`${response.user} (${response.date}): ${response.message}`);
                break;
            case 'USER_UPDATE':
                console.log(response.user + " type " +response.message);
                if (response.user === '') {
                    document.getElementById('chatUsers').innerHTML = '';
                } else {
                    users.innerHTML += `${response.user} (${response.message})` +'\n';
                }
                break;
        }
    };

    webSocket.onclose = function(event){
        document.getElementById('chatView').setAttribute('style', 'display: none');
        document.getElementById('connectView').removeAttribute('style');
        document.getElementById('chatWindow').innerHTML = '';
        alert('Disconnected from server');
    };

    webSocket.onopen = function(event){
        sendConnectRequest();
    };
}

function sendConnectRequest() {
    msg =  {
        message: '',
        user: user,
        date: '',
        type: 'CONNECT'
    };
    webSocket.send(JSON.stringify(msg));
}

/**
 * Sends the value of the text input to the server
 */
function send(){
    const input = document.getElementById("input");
    msg.message = input.value;
    input.value = '';
    msg.user = user;
    msg.type = 'MESSAGE';
    webSocket.send(JSON.stringify(msg));
}

function closeSocket(){
    msg.message = '';
    msg.user = user;
    msg.type = 'DISCONNECT';
    webSocket.send(JSON.stringify(msg));
    webSocket.close();
}

function writeResponse(text){
    messages.innerHTML += text + "\n";
}