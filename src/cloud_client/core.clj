(ns cloud-client.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.format :as time]))

(declare auth-headers)

(defrecord Drop [id name private? trash? views created])

(defn- value-for-name
  [name data]
  (:value (first (filter #(= name (:name %)) data))))

(defn- get-token
  "User token for CloudApp access"
  [user pass]
  (let [resp (client/get "https://api.getcloudapp.com/authorization"
                         {:basic-auth [user pass]})
        body (json/read-str (:body resp) :key-fn keyword)
        data (:data (first (:items (:collection body))))]
    (:value (first (filter #(= "token" (:name %)) data)))))

(defn- make-drop
  [drop-data]
  (->Drop (value-for-name "id" drop-data)
          (value-for-name "name" drop-data)
          (value-for-name "private" drop-data)
          (value-for-name "trash" drop-data)
          (value-for-name "views" drop-data)
          (time/parse (value-for-name "created" drop-data))))

(defn- get-link
  [link-name]
  (let [resp (client/get "https://api.getcloudapp.com/" {:headers auth-headers})
        body (json/read-str (:body resp) :key-fn keyword)
        links (:links (:collection body))]
    (:href (first (filter #(= link-name (:rel %)) links)))))

(defn setup
  "Get authentication token to connect to CloudApp"
  [user pass]
  (let [token (get-token user pass)]
    (def auth-headers {"Authorization" (str "Token token=" token)})))

(defn drops
  "List your drops"
  [& {:keys [page per-page] :or {page 1 per-page 10}}]
  (let [drops-link (get-link "drops")
        resp (client/get drops-link
                         {:headers auth-headers
                          :query-params { :page page :per_page per-page }})
        data (:items (:collection (json/read-str (:body resp) :key-fn keyword)))]
    (map #(make-drop (:data %)) data)))
