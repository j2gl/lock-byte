const { ipcRenderer } = require('electron');

let authToken = null;
let countdown = 30;

// Handle login
async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    console.log("username:", username); // Debugging

    const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `username=${username}&password=${password}`
    });

    if (response.ok) {
        console.log("Received Token:", authToken); // Debugging
        authToken = await response.text();
        document.getElementById('auth').style.display = 'none';
        document.getElementById('app').style.display = 'block';
        loadAccounts();
    } else {
        alert("Login failed");
    }
}

// Handle logout
function logout() {
    authToken = null;
    document.getElementById('auth').style.display = 'block';
    document.getElementById('app').style.display = 'none';
}


// Fetch accounts from backend
async function loadAccounts() {
    const response = await fetch("http://localhost:8080/api/otp/accounts", {
        headers: { "Authorization": `Bearer ${authToken}` }
    });

    console.log("Authorization", authToken); // Debugging

    if (!response.ok) {
        alert("Session expired. Please log in again.");
        logout();
        return;
    }

    const accounts = await response.json();
    const list = document.getElementById('accountList');
    list.innerHTML = '';

    accounts.forEach(account => {
        const li = document.createElement('li');
        li.classList.add('list-group-item', 'list-group-item-action');
        li.innerText = account.accountName;
        li.onclick = () => getOTP(account.accountName);
        list.appendChild(li);
    });
}

// Fetch OTP
async function getOTP(accountName) {
    if (!accountName) {
        alert('Please select an account');
        return;
    }

    const response = await fetch(`http://localhost:8080/api/otp/generate?accountName=${accountName}`, {
        headers: { "Authorization": `Bearer ${authToken}` }
    });

    if (response.ok) {
        const otp = await response.text();
        document.getElementById('otp').innerText = otp;
    } else {
        alert("Failed to fetch OTP");
    }
}

// Auto refresh OTP every 30 seconds
setInterval(() => {
    const selected = document.querySelector('.selected-account');
    if (selected) {
        getOTP(selected.innerText);
    }
}, 30000);
