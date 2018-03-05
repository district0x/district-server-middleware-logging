# district-server-middleware-logging

Simple Clojurescript [express.js](https://expressjs.com/) server middleware for logging requests and request errors.
It uses [timbre](https://github.com/ptaoussanis/timbre) as a logging library. 
This middleware is meant to be plugged into other district0x expressjs server modules.

## Installation
Add `[district0x/district-server-middleware-logging "1.0.0"]` into your project.clj  
Include `[district.server.middleware.logging]` in your CLJS file

## Usage
Here's example how to use with [district-server-graphql](https://github.com/district0x/district-server-graphql) server. 
 

```clojure
(ns my-district
    (:require [mount.core :as mount]
              [district.server.graphql]
              [district.server.middleware.logging :refer [logging-middlewares]]))

  (def schema "type Query { hello: String}")
  (def root {:hello (constantly "Hello world")})

  (-> (mount/with-args
        {:graphql {:port 6200
                   :path "/graphql"
                   :schema schema
                   :root-value root
                   :middlewares [logging-middlewares] ;; This installs middleware into the server
                   }})
    (mount/start))
```

That's it! Not the server will log each request and request error, according to how you had set up your timbre.
For easy timbre setup see [district-server-logging](https://github.com/district0x/district-server-logging). 
