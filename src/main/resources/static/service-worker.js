const CACHE_NAME = "shopeasy-v3";


const FILES_TO_CACHE = [

    "./",
    "./index.html",

    "./products.html",
    "./product-details.html",

    "./login.html",
    "./account.html",

    "./wishlist.html",
    "./cart.html",

    "./checkout.html",
    "./orders.html",

    "./loyalty.html",
    "./contact.html",

    "./style.css",
    "./script.js",

    "./manifest.json"

];


self.addEventListener(
    "install",
    function(event) {

        event.waitUntil(

            caches
                .open(CACHE_NAME)
                .then(
                    function(cache) {

                        return cache.addAll(
                            FILES_TO_CACHE
                        );

                    }
                )

        );


        self.skipWaiting();

    }
);


self.addEventListener(
    "activate",
    function(event) {

        event.waitUntil(

            caches
                .keys()
                .then(
                    function(cacheNames) {

                        return Promise.all(

                            cacheNames
                                .filter(
                                    name =>
                                        name !== CACHE_NAME
                                )
                                .map(
                                    name =>
                                        caches.delete(name)
                                )

                        );

                    }
                )

        );


        self.clients.claim();

    }
);


self.addEventListener(
    "fetch",
    function(event) {

        if (event.request.url.includes("/api/") || event.request.method !== "GET") {
            event.respondWith(fetch(event.request));
            return;
        }

        event.respondWith(

            caches
                .match(event.request)
                .then(
                    function(cachedResponse) {

                        if (cachedResponse) {

                            return cachedResponse;

                        }


                        return fetch(
                            event.request
                        );

                    }
                )

        );

    }
);