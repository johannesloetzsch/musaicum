(ns musaicum.lib.ajax.sync
  (:require [clojure.core.async :refer [chan go >! <! #?(:clj <!!)]]
            [ajax.core :refer [POST json-request-format json-response-format]]))

(defn channeled [method url &[{:as args :keys [handler error-handler] :or {handler identity error-handler identity}}]]
  (let [c (chan)]
       (method url (merge args
                   {:handler #(go (>! c (handler %)))
                    :error-handler #(go (>! c (error-handler %)))}))
       c))

(defn awaited [method url args]
  #?(:clj
    (<!! (channeled method url args))))


(comment
  ;; async
  (POST "https://petstore.swagger.io/v2/pet"
        {:format (json-request-format)
         :params {:name "doggie"}
         :response-format (json-response-format {:keywords? true})})
  ;; sync
  (go (prn (<! (channeled POST "https://petstore.swagger.io/v2/pet"
                          {:format (json-request-format)
                           :params {:name "doggie"}
                           :response-format (json-response-format {:keywords? true})}))))
  (awaited POST "https://petstore.swagger.io/v2/pet"
           {:format (json-request-format)
            :params {:name "doggie"}
            :response-format (json-response-format {:keywords? true})
            :handler :id}))
