(ns neubite.middleware
  (:use ring.util.response)
  (:require [neubite.models :refer [get-user-by-email]]
            [clojure.stacktrace :as st]
            [clojure.pprint :refer [pprint]]))

(defn wrap-if [handler pred wrapper & args]
  (if pred
    (apply wrapper handler args)
    handler))

(defn wrap-failsafe
  ([handler]
    (wrap-failsafe handler (fn [req e]
                             (str "<h1>Oops</h1>\n"
                                  "<pre>"
                                  (with-out-str (pprint req))
                                  "</pre>"
                                  "<br><br>"
                                  e "<br><br>"
                                  "<pre>"
                                  (with-out-str
                                    (st/print-stack-trace e))
                                  "</pre>"
                                  "<br><br>"))))
  ([handler body-renderer]
    (fn [req]
      (try
        (handler req)
        (catch Throwable e
          {:status 500
           :headers {"Content-Type" "text/html"}
           :body (body-renderer req e)})))))

(defn user-middleware [app]
  (fn [req]
    (let [email (:user-email (:session req))
          user (if (not (= email "")) (get-user-by-email email) nil)
          superuser (:is_superuser user)
          staff (:is_staff user)]
      (if user
        (let [req (assoc req :user user :superuser superuser :staff staff)]
          (app req))
        (app req)))))

(defn call-handler [fn args]
  (apply fn args))

(defn superuser-only [fn & args]
  (if (:superuser (first args))
    (call-handler fn args)
    (redirect "/")))

(defn staff-only [fn & args]
  (if (:staff (first args))
    (call-handler fn args)
    (redirect "/")))

(defn print-middleware [app]
  (fn [req]
    (println req)
    (app req)))
