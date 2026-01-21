const {app, BrowserWindow} = require('electron')
const path = require ('path')

function createWindow(){
    const mainWindow = new BrowserWindow ({
        width: 1200,
        height: 800,
    })
    mainWindow.loadFile('indices/index.html')
}

app.whenReady().then(() => {
    createWindow()

    app.on('activate', function(){
        if (BrowserWindow.getAllWindows().length === 0) createWindow()
    })
})