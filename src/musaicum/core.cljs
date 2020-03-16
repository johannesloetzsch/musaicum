(ns musaicum.core
  (:require [reagent.dom :as rd]
            [musaicum.state :as state :refer [app-state load-images img-attrs->state+arrange decoupled-arrange]]))

(defn app []
  [:div {:style {:height "100%" :width "100%"}}
    [:div {:style {:position "relative" :height "100%" :width "100%" #_#_:overflow "scroll"}
           :class "musaicum overview-size binpacking"}
          (for [[img-src img-data] (:imgs @app-state)]
               [:img {:key img-src
                      :id img-src
                      :src img-src
                      :onLoad img-attrs->state+arrange}])]
    #_[:div {:style {:position "relative" :height "100%" :width "100%" #_#_:overflow "scroll"}
           :class "musaicum overview-size"}
          (for [[img-src img-data] (:imgs @app-state)]
               [:img {:key img-src
                      :id img-src
                      :src img-src
                      :onLoad img-attrs->state+arrange}])]])

(defn start []
  ;; called on every reload during development
  (load-images)
  (js/window.addEventListener "resize" decoupled-arrange)
  (rd/render [app]
             (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  #_(js/console.log "stop"))
