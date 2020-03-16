(ns musaicum.arrange.core
  (:require [musaicum.arrange.overview-size]
            [musaicum.arrange.binpacking]))

(defn arrange-all [app-state]
  (doseq [parentElement (js/Array.from (js/document.querySelectorAll ".musaicum.overview-size"))]
         (musaicum.arrange.overview-size/arrange parentElement app-state))
  (doseq [parentElement (js/Array.from (js/document.querySelectorAll ".musaicum.binpacking"))]
         (musaicum.arrange.binpacking/arrange parentElement app-state)))
