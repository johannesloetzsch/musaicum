# musaicum

**Mosaic generation by algorithms for 2d bin packing**

## Run

``` shell
yarn install

yarn watch
```

[http://localhost:8700/](http://localhost:8700/)

## Cljs-nRepl

```shell
clj -Sdeps '{:deps {nrepl {:mvn/version "0.8.0"}}}' -m nrepl.cmdline --connect --port 8777
(require 'shadow.cljs.devtools.api)
(shadow.cljs.devtools.api/repl :app)
```

## Clean

``` shell
yarn clean
```

## Release

``` shell
yarn release
```

## License

Copyright © 2020 Johanes Lötzsch

Distributed under the Eclipse Public License either version 1.0 or any later version.
