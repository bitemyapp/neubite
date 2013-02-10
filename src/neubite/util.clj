(ns neubite.util)

(defn dissoc-in
  "Dissociates an entry from a nested associative structure where ks is a
  sequence of keys and returns a new nested structure."
  {:static true}
  [m [k & ks]]
  (if ks
    (assoc m k (dissoc-in (get m k) ks))
    (dissoc m k)))

(defn format-time
  "formats the time using SimpleDateFormat, the default format is
   \"dd MMM, yyyy\" and a custom one can be passed in as the second argument"
  ([time] (format-time time "dd MMM, yyyy"))
  ([time fmt]
    (.format (new java.text.SimpleDateFormat fmt) time)))
