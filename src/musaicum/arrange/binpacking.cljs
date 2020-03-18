(ns musaicum.arrange.binpacking
  (:require [musaicum.algo.binpacking1d.first-fit]))

(defn mapslist->map [mapslist &[{:keys [id] :or {id :id}}]]
  (zipmap (map id mapslist) mapslist))

(defn px [& nums]
  (-> (reduce + nums)
      int (str "px")))

(defn arrange [imgsFragment binElements app-state &[{:keys [scale_x scale_y filling-axis]
                                                      :or {scale_x (/ 1 3) scale_y 1 #_(/ 1 3) filling-axis #_:width :height}}]]
  (let [imgElements (js/Array.from (.querySelectorAll imgsFragment "img"))
        ids (map #(.-id %) imgElements)
        bins (map (fn [binElement]
                      {:filling-axis (if (some #{"horizontal"} (js/Array.from (.-classList binElement)))
                                         :width :height)
                       :width (.-clientWidth binElement)
                       :height (.-clientHeight binElement)
                       :element binElement})
                  binElements)
        binpacking-args {:items (vals (select-keys (:imgs @app-state) ids))
                         :bins bins}
        binpacking-results (musaicum.algo.binpacking1d.first-fit/binpack binpacking-args)
        imgs (->> (for [bin binpacking-results]
                       (for [item (:items bin)]
                            (assoc item :bin (dissoc bin :items))))
                  (apply concat))]
       (doseq [attrs imgs
               :let [imgElement (.getElementById imgsFragment (:id attrs))
                     destinationBinElement (get-in attrs [:bin :element])]]
              (when (and imgElement destinationBinElement)
                    (aset imgElement "style" "zIndex" (- (:area attrs)))
                    (aset imgElement "style" "left" (px (get-in attrs [:bin :x]) (:x attrs)))
                    (aset imgElement "style" "top" (px (get-in attrs [:bin :y]) (:y attrs)))
                    (aset imgElement "style" "width" (px (:width attrs)))
                    (aset imgElement "style" "height" (px (:height attrs)))
                    (.appendChild destinationBinElement imgElement)))))
