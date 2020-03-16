(ns musaicum.state
  (:require [reagent.core :as r]
            [musaicum.img-src.archive-org :refer [query-img-ids each-img-url]]
            [musaicum.arrange.core :refer [arrange-all]]))

(defonce app-state (r/atom {:imgs {}}))

(defn load-images
  []
  (if (empty? (:imgs @app-state))
      (let [assoc-img-urls (fn [query-response]
                               (each-img-url query-response
                                             {:callback (fn [url] (swap! app-state update-in [:imgs url] #(or % {})))}))]
           (query-img-ids "collection:(solarsystemcollection)" {:callback assoc-img-urls :limit 15}))))


(defn decoupled-arrange [&[{:keys [ms] :or {ms 1000}}]]
  (if-let [oldTimeout (:decoupled-arrange @app-state)]
          (js/clearTimeout oldTimeout))
  (swap! app-state assoc :decoupled-arrange (js/setTimeout #(arrange-all app-state) ms)))


(defn img-attrs->state+arrange
  "onLoad() get attributes from every image and merge them into app-state; then arrange"
  [syntheticEvent &[{:keys [scale] :or {scale 0.15}}]]
  (let [imgElement (.-target syntheticEvent)
        id (.-src imgElement)
        if>0 #(if (> % 0) %)
        attrs (-> {:id id
                   :width (if>0 (.-naturalWidth imgElement))
                   :height (if>0 (.-naturalHeight imgElement))}
                  (#(assoc % :width (int (* scale (:width %)))
                             :height (int (* scale (:height %)))))
                  (#(assoc % :ratio (if (:height %) (/ (:width %) (:height %)))))
                  (#(assoc % :area (* (:width %) (:height %)))))]
       (swap! app-state update-in [:imgs id] merge attrs))
  (decoupled-arrange))
