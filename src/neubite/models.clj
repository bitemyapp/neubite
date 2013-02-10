(ns neubite.models
  (:use carica.core
        monger.operators
        slugify.core)
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [noir.util.crypt :as crypt]
            [clj-time.core :refer [now]]
            monger.joda-time)
  (:import [org.bson.types ObjectId]
           [org.joda.time DateTimeZone]))

(DateTimeZone/setDefault DateTimeZone/UTC)
(mg/connect-via-uri! (config :dburi))
(mc/ensure-index "users" {:email 1} {:unique true})
(mc/ensure-index "posts" {:slug 1} {:unique true})

(defn get-user-by-email [email]
  (mc/find-one-as-map "users" {:email email}))

(defn create-user [email password]
  (if (get-user-by-email email)
    nil
    (mc/insert-and-return "users" {:email email
                                   :date_created (now)
                                   :bcrypt_password (crypt/encrypt password)})))

(defn auth-user [email maybe-pass]
  (let [maybe-user (get-user-by-email email)]
    (when maybe-user
      (when (crypt/compare maybe-pass (:bcrypt_password maybe-user))
        maybe-user))))

(defn remove-user [email]
  (mc/remove "users" {:email email}))

(defn get-document-by-id [coll id]
  (mc/find-map-by-id coll (ObjectId. id)))

(defn update [coll id attrs]
  (let [existing (get-document-by-id coll id)
        updated (dissoc (merge existing attrs) :_id :file)]
    (mc/update-by-id coll id updated)))

(defn update-user-by-id [id attrs]
  (update "users" id attrs))

(defn update-user-password [id new_password]
  (update-user-by-id id {:bcrypt_password (crypt/encrypt new_password)}))

(defn make-superuser-by-id [id]
  (update-user-by-id id {:is_superuser true :is_staff true}))

(defn make-staff-by-id [id]
  (update-user-by-id id {:is_superuser nil :is_staff true}))

(defn create-post [title body]
  (mc/insert-and-return "posts" {:title title
                                 :body body
                                 :slug (slugify title)
                                 :date_created (now)}))

(defn get-most-recent-posts []
  [])
