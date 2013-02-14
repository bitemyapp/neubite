(ns neubite.models
  (:use monger.operators
        slugify.core)
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq]
            [noir.util.crypt :as crypt]
            [clj-time.core :refer [now]]
            [neubite.config :refer [config]]
            monger.joda-time)
  (:import [org.bson.types ObjectId]))

(defn to-object-id [id]
  (cond
   (instance? ObjectId id) id
   :else (ObjectId. id)))

(declare get-user-by-email)
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
  (mc/find-map-by-id coll (to-object-id id)))

(defn update [coll id attrs]
  (let [existing (get-document-by-id coll id)
        updated (dissoc (merge existing attrs) :_id :file)]
    (mc/update-by-id coll (to-object-id id) updated)))

(defn update-user-by-id [id attrs]
  (update "users" id attrs))

(defn update-user-password [id new_password]
  (update-user-by-id id {:bcrypt_password (crypt/encrypt new_password)}))

(defn make-superuser-by-id [id]
  (update-user-by-id id {:is_superuser true :is_staff true}))

(defn make-staff-by-id [id]
  (update-user-by-id id {:is_superuser nil :is_staff true}))

(defn get-user-by-email [email]
  (mc/find-one-as-map "users" {:email email}))

(defn create-post [title body]
  (mc/insert-and-return "posts" {:title title
                                 :body body
                                 :slug (slugify title)
                                 :date_created (now)
                                 :published nil}))

(defn get-post-by-slug [slug]
  (mc/find-one-as-map "posts" {:slug slug}))

(defn get-most-recent-posts []
  (mq/with-collection "posts"
    (mq/find {:published true})
    (mq/sort (sorted-map :date_created -1))))
