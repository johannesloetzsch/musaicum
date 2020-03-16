(ns musaicum.arrange.binpacking
  (:require [musaicum.algo.binpacking1d.first-fit]))

(defn mapslist->map [mapslist &[{:keys [id] :or {id :id}}]]
  (zipmap (map id mapslist) mapslist))

(defn px [& nums]
  (-> (reduce + nums)
      int (str "px")))

(defn arrange [parentElement app-state &[{:keys [scale_y] :or {scale_y 0.4}}]]
  (let [imgElements (js/Array.from (.querySelectorAll parentElement "img"))
        ids (map #(.-src %) imgElements)
        binpacking-args {:items (vals (select-keys (:imgs @app-state) ids))
                         :bins (iterate (fn [lastbin] (update lastbin :y #(+ % (* scale_y (.-clientHeight parentElement)))))
                                        {:x 0 :y 0 :width (.-clientWidth parentElement) :height (* scale_y (.-clientHeight parentElement))})}
        binpacking-results (musaicum.algo.binpacking1d.first-fit/binpack binpacking-args)
        imgs (->> (for [bin binpacking-results]
                       (for [item (:items bin)]
                            (assoc item :bin (dissoc bin :items))))
                  (apply concat)
                  mapslist->map)]
       ;(js/console.log binpacking-results)
       (doseq [imgElement imgElements
               :let [attrs (get imgs (.-src imgElement))]]
              (prn (+ (get-in attrs [:bin :y]) (:y attrs)))
              (aset imgElement "style" "zIndex" (- (:area attrs)))
              (aset imgElement "style" "left" (px (get-in attrs [:bin :x]) (:x attrs)))
              (aset imgElement "style" "top" (px (get-in attrs [:bin :y]) (:y attrs)))
              (aset imgElement "style" "width" (px (:width attrs)))
              (aset imgElement "style" "height" (px (:height attrs))))))
