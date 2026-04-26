const CACHE_NAME = 'tetotask-v1';
const ASSETS = [
  './',
  './index.html',
  './manifest.json',
  'https://www.svgrepo.com/show/532797/bread.svg'
];

self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
  );
});

self.addEventListener('fetch', (e) => {
  e.respondWith(
    caches.match(e.request).then((res) => res || fetch(e.request))
  );
});
