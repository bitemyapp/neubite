(ns neubite.middleware
  (:use carica.core
        ring.util.response)
  (:require [neubite.models :refer [get-user-by-email]]))

(def ^:dynamic g (atom {}))
(def secret (config :secret))

(defn wrap-if [handler pred wrapper & args]
  (if pred
    (apply wrapper handler args)
    handler))

(defn put-context [key val]
  (swap! g assoc key val))

(defn context-middleware [app]
  (fn [req]
    (reset! g {})
    (app req)))

(defn user-middleware [app]
  (fn [req]
    (let [email (:user-email (:session req))
          user (if (not (= email "")) (get-user-by-email email) nil)
          superuser (:is_superuser user)
          staff (:is_staff user)]
      (when user
        (do
          (put-context :user user)
          (put-context :superuser superuser)
          (put-context :staff staff)))
      (app req))))

(defn call-handler [fn args]
  (apply fn args))

(defn superuser-only [fn & args]
  (if (:superuser @g)
    (call-handler fn args)
    (redirect "/")))

(defn staff-only [fn & args]
  (if (:staff @g)
    (call-handler fn args)
    (redirect "/")))

(defn print-middleware [app]
  (fn [req]
    (println req)
    (app req)))
