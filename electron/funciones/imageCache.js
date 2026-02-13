// Simple IndexedDB-based image cache that stores image data URLs
(function(){
  const DB_NAME = 'book_images_db';
  const STORE_NAME = 'images';
  const DB_VERSION = 1;
  const TTL = 30 * 24 * 60 * 60 * 1000; // 30 days

  function openDB(){
    return new Promise((resolve, reject) => {
      const req = indexedDB.open(DB_NAME, DB_VERSION);
      req.onupgradeneeded = () => {
        const db = req.result;
        if (!db.objectStoreNames.contains(STORE_NAME)) {
          db.createObjectStore(STORE_NAME, { keyPath: 'url' });
        }
      };
      req.onsuccess = () => resolve(req.result);
      req.onerror = () => reject(req.error);
    });
  }

  function getEntry(db, url){
    return new Promise((resolve, reject) => {
      const tx = db.transaction(STORE_NAME, 'readonly');
      const store = tx.objectStore(STORE_NAME);
      const r = store.get(url);
      r.onsuccess = () => resolve(r.result);
      r.onerror = () => reject(r.error);
    });
  }

  function putEntry(db, entry){
    return new Promise((resolve, reject) => {
      const tx = db.transaction(STORE_NAME, 'readwrite');
      const store = tx.objectStore(STORE_NAME);
      const r = store.put(entry);
      r.onsuccess = () => resolve(r.result);
      r.onerror = () => reject(r.error);
    });
  }

  function blobToDataURL(blob){
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result);
      reader.onerror = () => reject(reader.error);
      reader.readAsDataURL(blob);
    });
  }

  async function getCachedImageDataUrl(url){
    if (!url) return null;
    try {
      const db = await openDB();
      const entry = await getEntry(db, url);
      // Return cached image or cached failure state
      if (entry && entry.timestamp && (Date.now() - entry.timestamp) < TTL) {
        if (entry.dataUrl) {
          return entry.dataUrl;
        }
        if (entry.failed) {
          // Return fallback logo if this URL previously failed (e.g., 429)
          return '../assets/imagenes/logo.png';
        }
      }

      // Fetch image and store
      const resp = await fetch(url, { mode: 'cors' });
      if (!resp.ok) throw new Error(`Image fetch failed: ${resp.status}`);
      const blob = await resp.blob();
      const dataUrl = await blobToDataURL(blob);
      try { await putEntry(db, { url, dataUrl, timestamp: Date.now(), failed: false }); } catch(e){ console.warn('Failed to store image in IDB', e); }
      return dataUrl;
    } catch (err) {
      console.warn('Image cache error for', url, err);
      // Cache the failure so we don't retry this URL
      try {
        const db = await openDB();
        await putEntry(db, { url, failed: true, timestamp: Date.now() });
      } catch(e){ console.warn('Failed to cache error state', e); }
      // Return fallback logo instead of original URL
      return '../assets/imagenes/logo.png';
    }
  }

  // Non-blocking cache peek: returns cached data URL if available, otherwise original URL
  // Does NOT fetch or cache new images—just checks if already cached
  async function getCachedImageIfAvailable(url) {
    if (!url) return null;
    try {
      const db = await openDB();
      const entry = await getEntry(db, url);
      if (entry && entry.timestamp && (Date.now() - entry.timestamp) < TTL) {
        if (entry.dataUrl) {
          return entry.dataUrl; // Return cached image data URL
        }
        if (entry.failed) {
          return '../assets/imagenes/logo.png'; // Return fallback for cached failures
        }
      }
    } catch (err) {
      // Silently ignore cache errors
    }
    // If not in cache, return original URL for browser to load naturally
    return url;
  }

  // Expose globally for existing scripts
  window.getCachedImageDataUrl = getCachedImageDataUrl;
  window.getCachedImageIfAvailable = getCachedImageIfAvailable;
})();
