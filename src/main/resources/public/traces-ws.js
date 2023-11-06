window.onload = setupWebSocket;
window.onhashchange = setupWebSocket;

console.log('ws')
if (!window.location.hash) {
    console.log('ws create hash')
    const newDocumentId = Date.now().toString(36); // this should be more random
    window.history.pushState(null, null, "#" + newDocumentId);
}

function setupWebSocket() {
    console.log('ws setupWebSocket')
    var textArea = document.querySelector("#textresult");
    if (window.location.hash.substr(1).length == 0) return;
    //const ws = new WebSocket(`ws://localhost:7000/docs/${window.location.hash.substr(1)}`);
    //const ws = new WebSocket(`ws://localhost:7000/docs/1111`);
    var origin = location.origin
    var server = origin.substring(origin.lastIndexOf('/') + 1);
    var docURL = "ws://"+server+"/docs/1111";
    console.log(docURL);
    const ws = new WebSocket(docURL);
    textArea.onkeyup = () => ws.send(textArea.value);
    ws.onmessage = msg => {
        console.log('ws onmessage')
        textArea = document.querySelector("#textresult");
        if (textArea) {
            const offset = msg.data.length - textArea.value.length;
            const selection = {start: textArea.selectionStart, end: textArea.selectionEnd};
            const startsSame = msg.data.startsWith(textArea.value.substring(0, selection.end));
            const endsSame = msg.data.endsWith(textArea.value.substring(selection.start));
            textArea.value = msg.data;
            if (startsSame && !endsSame) {
                textArea.setSelectionRange(selection.start, selection.end);
            } else if (!startsSame && endsSame) {
                textArea.setSelectionRange(selection.start + offset, selection.end + offset);
            } else { // this is what google docs does...
                textArea.setSelectionRange(selection.start, selection.end + offset);
            }
        }
    };
    ws.onclose = setupWebSocket; // should reconnect if connection is closed
}