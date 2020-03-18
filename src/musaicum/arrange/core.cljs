(ns musaicum.arrange.core
  (:require [musaicum.arrange.binpacking]))

(defn arrange-all [app-state]
  (let [itemElements (js/Array.from (js/document.querySelectorAll ".musaicum.item"))
        binElements (js/Array.from (js/document.querySelectorAll ".musaicum.bin:not(.fallback)"))
        fallbackBinElement (js/document.querySelector ".musaicum.fallback")
        fragment (js/document.createDocumentFragment)]
       (doseq [itemElement itemElements]
              (.appendChild fragment itemElement))
       (musaicum.arrange.binpacking/arrange fragment binElements app-state)
       (.appendChild fallbackBinElement fragment)))
