(ns musaicum.algo.binpacking1d.first-fit)

(defn first-match-index [coll pred]
  (first (keep-indexed (fn [n item] (if (pred item) n)) coll)))

(defn used-one-dimension [bin axis]
  (->> (map axis (:items bin))
       (reduce +)))

(defn fit1d? [item bin]
  (let [remaining-width (- (:width bin) (used-one-dimension bin :width))
        remaining-height (- (:height bin) 0 #_(used-one-dimension bin :height))]
       (and (<= (:width item) remaining-width)
            (<= (:height item) remaining-height))))

(defn fit? [item bin]
  (fit1d? item bin))

(defn binpack* [{:keys [items bins] :as args}]
  (if (empty? items)
      bins
      (let [item (first items)
            first-fit-bin (first-match-index bins #(fit? item %))]
           (if-not first-fit-bin
                   ;; we ignore items not fitting into any bin
                   (recur (assoc args :items (rest items)))
                   (recur (assoc args :items (rest items)
                                      :bins (concat (take first-fit-bin bins)
                                                    [(let [bin (nth bins first-fit-bin)]
                                                          (update bin :items #(concat % [(assoc item
                                                                                                :x (used-one-dimension bin :width)
                                                                                                :y 0)])))]
                                                    (drop (inc first-fit-bin) bins))))))))

(defn binpack [{:keys [items bins] :as args}]
  (binpack* (assoc args :items (sort-by :area > items)
                        :bins (take (count items) bins))))
