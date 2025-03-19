const { ipcRenderer } = require('electron');

async function getOTP() {
    const username = document.getElementById('username').value;
    if (!username) {
        alert('Please enter a username');
        return;
    }

    const otp = await ipcRenderer.invoke('fetch-otp', username);
    document.getElementById('otp').innerText = otp;
}

