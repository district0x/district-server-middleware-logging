(ns district.server.middleware.logging
  (:require
    [taoensso.timbre :as logging]))

(def sensitive-headers #{:authorization :cookie})

(defn- hide-sensitive-headers [headers]
  (reduce-kv (fn [m k v]
               (if (contains? sensitive-headers k)
                 (assoc m k "<hidden>")
                 (assoc m k v)))
             {} (js->clj headers :keywordize-keys true)))

(defn- get-fields [req]
  (reduce (fn [acc key]
            (assoc acc key (let [field-value (aget req (name key))]
                             (if (= key :headers)
                               (hide-sensitive-headers field-value)
                               field-value))))
          {}
          [:path :ip :protocol :method :params :hostname :httpVersion :headers :url :query]))

(defn- jsobj->clj [obj]
  (reduce (fn [acc key]
            (assoc acc (keyword key) (aget obj key)))
          {}
          (js->clj (js-keys obj))))

(defn logging-middleware [req res next]
  (logging/info "Server Request" {:request (get-fields req)})
  (next))

(defn error-logging-middleware [err req res next]
  (let [error (cond
                (instance? js/Error err) (aget err "message")
                (object? err) (jsobj->clj err)
                :else (str err))]
    (logging/error "Server Request Error" {:request (get-fields req)
                                           :error error})
    (.status res 500)))

(def logging-middlewares [logging-middleware error-logging-middleware])
