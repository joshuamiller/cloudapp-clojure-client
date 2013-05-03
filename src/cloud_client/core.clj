(ns cloud-client.core
  (:require [clj-time.format :as time]
            [collection-json.client :as client]))

(declare auth-header)
(def root-url "https://api.getcloudapp.com/")
(def authorization-url (str root-url "authorization"))

(defn- make-drop
  [item]
  {:id (client/get-value "id" item)
   :name (client/get-value "name" item)
   :private? (client/get-value "private" item)
   :trash? (client/get-value "trash" item)
   :views (client/get-value "views" item)
   :created (time/parse (client/get-value "created" item))})

(defn- get-collection
  [url token]
  (client/get-collection url {:headers (auth-header token)}))

(defn follow-link
  [name coll token]
  (client/follow-link name coll {:headers (auth-header token)}))

(defn- auth-header
  [token]
  {"Authorization" (str "Token token=" token)})

(defn get-token
  [email pass]
  (let [coll (client/get-collection authorization-url {:basic-auth [email pass]})]
    (client/get-value "token" (first (:items coll)))))

(defn root-collection
  "Get authentication token to connect to CloudApp"
  [token]
  (get-collection root-url token))

(defn drops
  [coll token]
  (->> (follow-link "drops" coll token)
       :items
       (map make-drop)))

(defn create-drop
  [values coll token]
  (client/post-item values coll {:headers (auth-header token)}))

(defn update-drop
  [values coll item token]
  (client/put-item (merge (make-drop item) values) coll item {:headers (auth-header token)}))
