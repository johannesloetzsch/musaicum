(ns musaicum.img-src.local
  (:require [ajax.core :refer [GET json-response-format]]))

(defn relative-url [path]
  (clojure.string.join "/" (-> (.-href js/document.location)
                               (clojure.string.split "/")
                               butlast
                               (concat [path]))))


(defn query-images [url &[{:keys [callback] :or {callback prn}}]]
  (GET url
       {:response-format (json-response-format {:keywords? true})
        :handler callback}))

(comment (query-images (relative-url "example.json")))


(defn assoc-into-app-state [app-state loader {:keys [files]}]
  (doseq [file files]
         (swap! app-state assoc-in [:imgs file :loader] loader)))


(defn load-images [app-state loader {:keys [url]}]
  (query-images (relative-url url) {:callback (partial assoc-into-app-state app-state loader)}))
