(defproject neubite "0.0.4"
  :description "Portfolio site"
  :url "http://www.bitemyapp.com/"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.3.5"]
                 [clj-time "0.4.4"]
                 [compojure "1.1.5"]
                 [selmer "0.4.0"]
                 [http-kit "2.0.0"]
                 [ring-server "0.2.8"]
                 [slugify "0.0.1"]
                 [bulwark "0.0.1"]
                 [com.taoensso/timbre "1.2.0"]
                 [com.taoensso/tower "1.2.0"]
                 [com.taoensso/carmine "2.2.0"]
                 [com.novemberain/monger "1.4.2"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler neubite.handler/war-handler
         :init    neubite.handler/init
         :destroy neubite.handler/destroy}
  :aot :all
  :main neubite.handler
  :profiles
  {:production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.8"]]}}
  :min-lein-version "2.0.0")
