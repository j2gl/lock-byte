const { ipcRenderer } = require('electron');

let countdown = 30;

// Fetch and display accounts
async function loadAccounts() {
    try {
        const accounts = await ipcRenderer.invoke('fetch-accounts');
        const list = document.getElementById('accountList');
        list.innerHTML = '';

        accounts.forEach(account => {
            const li = document.createElement('li');
            li.classList.add('list-group-item', 'list-group-item-action');
            li.innerText = account.accountName;
            li.onclick = () => getOTP(account.accountName);
            list.appendChild(li);
        });
    } catch (error) {
        console.error('Error loading accounts', error);
    }
}

// Fetch OTP
async function getOTP(accountName) {
    if (!accountName) {
        alert('Please select an account');
        return;
    }

    const otp = await ipcRenderer.invoke('fetch-otp', accountName);
    document.getElementById('otp').innerText = otp;

    countdown = 30;
}

// Auto refresh OTP every 30 seconds
setInterval(() => {
    countdown--;
    document.getElementById('timer').innerText = `Next update: ${countdown}s`;
    if (countdown === 0) {
        const selected = document.querySelector('.selected-account');
        if (selected) {
            getOTP(selected.innerText);
        }
    }
}, 1000);

// Load accounts on startup
window.onload = loadAccounts;
