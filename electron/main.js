const {app, BrowserWindow, ipcMain} = require('electron')
const path = require('path')
const fs = require('fs')
const http = require('http')
const https = require('https')
const crypto = require('crypto')
const {pathToFileURL} = require('url')

// Simple disk cache for book cover images
const CACHE_DIR = path.join(app.getPath('userData'), 'book_covers');
let ongoing = new Map(); // url -> Promise

function ensureCacheDir(){
    try { fs.mkdirSync(CACHE_DIR, { recursive: true }); } catch(e) { /* ignore */ }
}

function urlToFilename(url){
    const hash = crypto.createHash('sha1').update(url).digest('hex');
    // try to preserve extension
    let ext = '.jpg';
    try {
        const u = new URL(url);
        const p = u.pathname;
        const m = p.match(/\.([a-zA-Z0-9]{2,6})$/);
        if (m) ext = '.' + m[1];
    } catch(e){}
    return hash + ext;
}

function fetchToFile(url, filePath){
    return new Promise((resolve, reject) => {
        const client = url.startsWith('https') ? https : http;
        const req = client.get(url, (res) => {
            if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
                // follow redirect
                fetchToFile(res.headers.location, filePath).then(resolve).catch(reject);
                return;
            }
            if (res.statusCode !== 200) return reject(new Error('Status ' + res.statusCode));
            const tmp = filePath + '.tmp';
            const fileStream = fs.createWriteStream(tmp);
            res.pipe(fileStream);
            fileStream.on('finish', () => {
                fileStream.close(() => {
                    try { fs.renameSync(tmp, filePath); } catch(e) { /* ignore */ }
                    resolve(filePath);
                });
            });
            fileStream.on('error', (err) => reject(err));
        });
        req.on('error', reject);
        req.setTimeout(20000, () => { req.abort(); reject(new Error('timeout')); });
    });
}

ipcMain.handle('get-cover-file', async (event, url) => {
    if (!url) return null;
    ensureCacheDir();
    const fname = urlToFilename(url);
    const filePath = path.join(CACHE_DIR, fname);
    // If exists, return immediately
    try {
        if (fs.existsSync(filePath)) return pathToFileURL(filePath).href;
    } catch(e){}

    // If already fetching, await that
    if (ongoing.has(url)) return (await ongoing.get(url)) || null;

    const p = (async () => {
        try {
            const fp = await fetchToFile(url, filePath);
            return pathToFileURL(fp).href;
        } catch (e) {
            try { if (fs.existsSync(filePath)) fs.unlinkSync(filePath); } catch(_){}
            return null;
        } finally {
            ongoing.delete(url);
        }
    })();
    ongoing.set(url, p);
    return await p;
});

function createWindow(){
    const mainWindow = new BrowserWindow ({
        width: 1400,
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