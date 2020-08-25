(ns musaicum.img-src.dropbox
  (:require [clojure.core.async :refer [go <!]]
            [musaicum.lib.ajax.sync :refer [channeled]]
            [ajax.core :refer [GET POST json-request-format json-response-format]]))

(defn encodeURI [s]
  #?(:cljs (js/encodeURI s)))

(defn json [edn]
  #?(:cljs (js/JSON.stringify (clj->js edn))))

(defn dropbox-path->link [bearer path]
  (channeled POST "https://api.dropboxapi.com/2/sharing/list_shared_links"
                  {:headers {"Authorization" (str "Bearer " bearer)}
                   :format (json-request-format)
                   :params {:path path}
                   :response-format (json-response-format {:keywords? true})
                   :handler #(-> % :links first :url)}))

(defn dropbox-path->listing [bearer path &[{:keys [recursive limit] :or {recursive false limit 100}}]]
  (channeled POST "https://api.dropboxapi.com/2/files/list_folder"
                  {:headers {"Authorization" (str "Bearer " bearer)}
                   :format (json-request-format)
                   :params {:path path :recursive recursive :limit limit}
                   :response-format (json-response-format {:keywords? true})
                   :handler (fn [result] (->> result :entries (map #(select-keys % [:.tag :path_display])))) }))


(defn query-images [bearer path limit &[{:keys [callback] :or {callback prn}}]]
  (go
    (let [link (<! (dropbox-path->link bearer path))
          files (->> (<! (dropbox-path->listing bearer path {:recursive true :limit limit}))
                     (filter #(= "file" (:.tag %)))
                     (map :path_display))]
         (callback {:files (for [f files
                                 :let [relative-path (clojure.string/replace f path "")]]
                                (str "https://content.dropboxapi.com/2/sharing/get_shared_link_file"
                                   "?authorization=" (encodeURI (str "Bearer " bearer))
                                   "&arg=" (encodeURI (json {:url link :path relative-path}))))}))))

(defn assoc-into-app-state [app-state loader {:keys [files]}]
  (doseq [file files]
         (swap! app-state assoc-in [:imgs file :loader] loader)))


(defn load-images [app-state loader {:keys [bearer path limit] :or {limit 10}}]
  (query-images bearer path limit {:callback (partial assoc-into-app-state app-state loader)}))
