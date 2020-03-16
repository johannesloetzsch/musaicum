(ns musaicum.img-src.archive-org
  (:require [ajax.core :refer [GET json-response-format]]))

(defn- img-url [details-response]
  (->> details-response :files
       (filter #(#{"JPEG"} (:format %)))
       first :name))

(defn query-img-url [id &[{:keys [callback] :or {callback prn}}]]
  (GET (str "https://archive.org/metadata/" id)
       {:response-format (json-response-format {:keywords? true})
        :handler #(callback (str "https://archive.org/download/" id "/" (img-url %)))}))

(comment (query-img-url "SPD-SLRSY-826")
           #_"https://archive.org/download/SPD-SLRSY-826/Cassini_launch.jpg")


(defn each-img-url [query-response &[{:keys [callback] :or {callback prn}}]]
  (doseq [id (->> query-response
                  :response :docs
                  (map :identifier))]
         (query-img-url id {:callback callback})))

(defn query-img-ids [query &[{:keys [callback limit page] :or {callback prn limit 5 page 1}}]]
  (GET "https://archive.org/advancedsearch.php"
       {:params {"output" "json"
                 "q" (str query " AND mediatype:(image)")
                 "fl[]" "identifier"
                 "rows" limit "page" page}
        :response-format (json-response-format {:keywords? true})
        :handler callback}))

(comment (query-img-ids "collection:(solarsystemcollection)")
         (query-img-ids "collection:(solarsystemcollection)" {:callback each-img-url}))
