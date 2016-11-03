(ns projectname.app.config
  "Configuration used for this application.")

(def routes
  "Bidi routes used for both server and client."
  ["/"
   [["" :home]
    [true :not-found]]])
