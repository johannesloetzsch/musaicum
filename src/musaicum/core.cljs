(ns musaicum.core
  (:require [reagent.dom :as rd]
            [musaicum.state :as state :refer [app-state load-all-images img-attrs->state+arrange decoupled-arrange]]))

(defn images [loader]  ;; TODO only the images of this one loader
  [:div (for [[img-src img-data] (:imgs @app-state)]
             [:img {:key img-src
                    :class "musaicum item"
                    :id img-src
                    :src img-src
                    :onLoad img-attrs->state+arrange}])])

(defn example-app []
  [:div {:class "grid" :style {:grid-template-areas "\"example1\" \"example2\"  \"fallback\""}}
    [:div {:style {:grid-area "example1"} :class "musaicum bin horizontal"} " "]
    [:div {:style {:grid-area "example2"} :class "musaicum bin horizontal"} " "]
    [:div {:style {:grid-area "fallback"} :class "musaicum bin fallback"}
		  [:div {:class "musaicum loader" :source "archive.org" :query "collection:(solarsystemcollection)" :limit "10"}]]])

(defn start []
  ;; called on every reload during development
  (if-let [parent (js/document.getElementById "example-app")]
          (rd/render [example-app] parent))
  (load-all-images)
  (doseq [loader (js/Array.from (js/document.querySelectorAll ".musaicum.loader"))]
         (rd/render [images loader] loader))
  (js/window.addEventListener "resize" decoupled-arrange))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  #_(js/console.log "stop"))
