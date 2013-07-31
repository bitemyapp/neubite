(ns neubite.config)

(def config {:dburi (or (get (System/getenv) "DATABASE_URL") "mongodb://localhost:27017/neubite")
             :secret (get (System/getenv) "SECRET_KEY")
             :ssl nil
             :admins ["cma@bitemyapp.com"]})
