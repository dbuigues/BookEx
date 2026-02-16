const { contextBridge, ipcRenderer } = require('electron');

// Exponemos una API mínima y segura al renderer
contextBridge.exposeInMainWorld('api', {
  invoke: (channel, payload) => {
    const validChannels = ['get-cover-file', 'listas:get', 'listas:create', 'listas:delete'];
    if (!validChannels.includes(channel)) {
      throw new Error('Canal IPC no permitido: ' + channel);
    }
    return ipcRenderer.invoke(channel, payload);
  }
});

