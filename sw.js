const CACHE_NAME = "tetotask-v8";
const ASSETS = [
  "./",
  "./index.html",
  "./manifest.json",
  "./icon-192.png",
  "./icon-512.png",
  "./icon-maskable-192.png",
  "./icon-maskable-512.png"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) =>
      Promise.allSettled(ASSETS.map((a) => cache.add(a)))
    )
  );
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

// Solo queste origini esterne vengono messe in cache (SDK Firebase e font)
const CACHEABLE_EXTERNAL = ["www.gstatic.com", "fonts.googleapis.com", "fonts.gstatic.com"];

self.addEventListener("fetch", (event) => {
  const req = event.request;
  if (req.method !== "GET") return;
  const url = new URL(req.url);
  const sameOrigin = url.origin === self.location.origin;

  // API (Firestore, Auth, Gemini, meteo...): sempre rete, mai cache
  if (!sameOrigin && !CACHEABLE_EXTERNAL.includes(url.hostname)) return;

  // Pagina dell'app: prima la rete (così vedi subito l'ultima versione),
  // la cache solo se sei offline.
  if (req.mode === "navigate" || (sameOrigin && url.pathname.endsWith("/index.html"))) {
    event.respondWith(
      fetch(req)
        .then((res) => {
          if (res && res.ok) {
            const copy = res.clone();
            caches.open(CACHE_NAME).then((c) => c.put("./index.html", copy));
          }
          return res;
        })
        .catch(() => caches.match("./index.html").then((r) => r || caches.match("./")))
    );
    return;
  }

  // Altri file: cache subito + aggiornamento in background
  event.respondWith(
    caches.open(CACHE_NAME).then((cache) =>
      cache.match(req).then((cached) => {
        const network = fetch(req)
          .then((res) => {
            if (res && (res.ok || res.type === "opaque")) cache.put(req, res.clone());
            return res;
          })
          .catch(() => cached);
        return cached || network;
      })
    )
  );
});

// Tocco sulla notifica o sui suoi pulsanti (✓ Fatto / ⏰ +10 min)
self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  const data = event.notification.data || {};
  const action = event.action;            // "" = tocco sul corpo della notifica
  const scope = self.registration.scope;
  event.waitUntil(
    self.clients.matchAll({ type: "window", includeUncontrolled: true }).then((wins) => {
      const win = wins.find((w) => w.url.startsWith(scope));
      if (action && data.taskId) {
        // L'app è aperta (anche in background): esegue l'azione senza portarla davanti
        if (win) { win.postMessage({ type: "notif-action", action, taskId: data.taskId }); return; }
        // App chiusa: la apre e le passa l'azione nell'URL
        return self.clients.openWindow(scope + "?notif=" + encodeURIComponent(action) + "&task=" + encodeURIComponent(data.taskId));
      }
      if (win && "focus" in win) return win.focus();
      return self.clients.openWindow(data.url || scope);
    })
  );
});
