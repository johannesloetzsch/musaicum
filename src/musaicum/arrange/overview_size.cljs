(ns musaicum.arrange.overview-size)

(defn arrange [parentElement app-state]
  (doseq [imgElement (js/Array.from (.querySelectorAll parentElement "img"))
          :let [attrs (get-in @app-state [:imgs (.-src imgElement)])]]
         (aset imgElement "style" "zIndex" (- (:area attrs)))
         (aset imgElement "style" "max-width" "100%")
         (aset imgElement "style" "max-height" "100%")))
