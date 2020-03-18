(ns musaicum.state
  (:require [reagent.core :as r]
            [musaicum.img-src.archive-org]
            [musaicum.arrange.core :refer [arrange-all]]))

(defonce app-state (r/atom {:imgs {}}))


(defn load-all-images []
  (let [loaders (js/Array.from (js/document.querySelectorAll ".musaicum.loader"))]
       (doseq [loader loaders
               :let [source (.getAttribute loader "source")]]
              (case source
                    "archive.org"
                      (musaicum.img-src.archive-org/load-images app-state {:query (.getAttribute loader "query")
                                                                           :limit (.getAttribute loader "limit")})
                    (js/console.error "Unknown `source`:" source)))))


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
