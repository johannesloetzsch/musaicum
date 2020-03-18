(ns musaicum.algo.binpacking1d.first-fit)

(defn first-match-index [coll pred]
  (first (keep-indexed (fn [n item] (if (pred item) n)) coll)))

(defn non-filling-axis [bin]
  (if (= :height (:filling-axis bin))
      :width
      :height))

(defn enlarged
  "fill an item up to the available space of the bin"
  [axis item bin {:keys [enlarge?]}]
  (let [ratio (if-not enlarge?
                      1
                      (/ (get bin (non-filling-axis bin))
                         (get item (non-filling-axis bin))))]
       (* ratio (axis item))))

(defn used-one-dimension [bin axis args]
  (if-not (= axis (:filling-axis bin))
          0
          (->> (map axis (:items bin))
               (reduce +))))

(defn fit1d? [item bin args]
  (let [remaining-width (- (:width bin) (used-one-dimension bin :width args))
        remaining-height (- (:height bin) (used-one-dimension bin :height args))]
       (and (<= (enlarged :width item bin args) remaining-width)
            (<= (enlarged :height item bin args) remaining-height))))

(defn fit? [item bin args]
  (fit1d? item bin args))

(defn binpack* [{:keys [items bins] :as args}]
  (if (empty? items)
      bins
      (let [item (first items)
            first-fit-bin (first-match-index bins #(fit? item % args))]
           (if-not first-fit-bin
                   ;; we ignore items not fitting into any bin
                   (recur (assoc args :items (rest items)))
                   (recur (assoc args :items (rest items)
                                      :bins (concat (take first-fit-bin bins)
                                                    [(let [bin (nth bins first-fit-bin)]
                                                          (update bin :items #(concat % [(assoc item
                                                                                                :x (used-one-dimension bin :width args)
                                                                                                :y (used-one-dimension bin :height args)
                                                                                                :width (enlarged :width item bin args)
                                                                                                :height (enlarged :height item bin args))])))]
                                                    (drop (inc first-fit-bin) bins))))))))

(defn binpack [{:keys [items bins enlarge?] :or {enlarge? true} :as args}]
  (binpack* (assoc args :items (sort-by :area > items)
                        :bins (take (count items) bins)
                        :enlarge? enlarge?)))
