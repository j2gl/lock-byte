const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const axios = require('axios');

let mainWindow;

app.whenReady().then(() => {
    mainWindow = new BrowserWindow({
        width: 400,
        height: 500,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false
        }
    });

    mainWindow.loadFile('index.html');
});

// Handle OTP requests
ipcMain.handle('fetch-otp', async (event, username) => {
    try {
        const response = await axios.get(`http://localhost:8080/api/otp/generate/${username}`);
        return response.data;
    } catch (error) {
        return 'Error fetching OTP';
    }
});

