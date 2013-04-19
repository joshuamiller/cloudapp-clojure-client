# cloud-client

A Clojure client for [CloudApp](http://getcloudapp.com/).

## Usage

```
user=> (setup "username" "pass")
user=> (def a-drop (first (drops :page 1 :per_page 1)))
user=> (:name a-drop)
"http://google.com"
```

## License

Copyright © 2013 Joshua Miller & Larry Marburger

Distributed under the Eclipse Public License, the same as Clojure.
