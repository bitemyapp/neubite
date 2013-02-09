{:dburi (get (System/getenv) "DATABASE_URL")
 :secret (get (System/getenv) "SECRET_KEY")
 :ssl (get (System/getenv) "SSL")
 :admins ["cma@bitemyapp.com"]
}
