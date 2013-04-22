(ns cloud-client.core
  (:require [clj-time.format :as time]
            [collection-json.client :as client]))

(declare auth-header)

(defrecord Drop [id name private? trash? views created])

(defn make-drop
  [item]
  (->Drop (client/get-value "id" item)
          (client/get-value "name" item)
          (client/get-value "private" item)
          (client/get-value "trash" item)
          (client/get-value "views" item)
          (time/parse (client/get-value "created" item))))

(defn- get-collection
  [url]
  (client/get-collection url {:headers auth-header}))

(defn- follow-link
  [name coll]
  (client/follow-link name coll {:headers auth-header}))

(defn get-token
  [user pass]
  (let [coll (client/get-collection "https://api.getcloudapp.com/authorization" {:basic-auth [user pass]})]
    (client/get-value "token" (first (:items coll)))))

(defn root-collection
  "Get authentication token to connect to CloudApp"
  [user pass]
  (let [token (get-token user pass)]
    (def auth-header {"Authorization" (str "Token token=" token)})
    (get-collection "https://api.getcloudapp.com/")))

(defn drops
  [coll]
  (follow-link "drops" coll))
