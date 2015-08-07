scaspell
========

build
-----

```
sbt package
```

run
---

```
sbt run
```

use
---

### spell checking

```shell
curl -v -X POST -d "you can\'t have it alll" \
'http://localhost:8080/spelling/check?lang=en&mode=email&limit=5'
```

The request body contains the text to be spell checked, the `lang` contains the language, the mode parameter refers to the aspell mode used and the limit parameter specifies the maximum number of suggestions to be returned for each spelling error.

Here's what the output looks like:

```json
{
  "alll": [
    "all",
    "Alla",
    "Alli",
    "Ally",
    "ally"
  ]
}
```

### list installed languages

```shell
curl 'http://localhost:8080/spelling/languages'
```

### list aspell modes

```shell
curl 'http://localhost:8080/spelling/modes'
```


### get aspell version

```shell
curl 'http://localhost:8080/spelling/version'
```

