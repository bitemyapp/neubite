(ns neubite.config)

(def config {:dburi (get (System/getenv) "DATABASE_URL")
             :secret (get (System/getenv) "SECRET_KEY")
             ; :ssl (get (System/getenv) "SSL")
             :ssl nil
             :admins ["cma@bitemyapp.com"]})
