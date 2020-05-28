# JUri

JUri is Java/kotlin library that can be used to convert Java/Kotlin Object into their URI representation. It can also be used to convert a URI string to an equivalent Java/Kotlin object. JUri can work with arbitrary pre-existing objects that you added a little annotations. 

Juri's basic concept and some method name has inspired from Google Gson.



### Goals

Provide simple `toUri()` and `fromUri()` methods to convert Java/Kotlin objects to URI and vice-versa

### Download

Gradle:

```groovy
repositories {
	maven { url "https://jitpack.io" }
}

dependencies {
	implementation "com.github.jaeho:juri:$juriVersion"
}
```



### How do I use Juri?

Simple Use cases will look something like this:

First, you have to declare uri model class using `@JuriModel` annotation. 

```kotlin
@JuriModel(scheme = "myapp", host = "search", path = "/{category}/p/{page}")
data class SearchOptions(
        val query: String,
        val sort: String?,
        val category: String?,
        val page: Int
)
```

If you want Uri model convert to string, look below.

```kotlin
Juri.toUri(SearchOptions("paris", "price:desc", "book", 1)
// It will be "myapp://search/book/p/1?query=paris&sort=price:desc"
```

Or vice-versa can possible.

```kotlin
Juri.fromUri("myapp://search/book/p/1?query=paris&sort=price:desc", SearchOptions::class.java)
// It will be SearchOptions("paris", "price:desc", "book", 1)
```

If you want to mark exceptive field, you can using `@JuriIgonre` and also you wanto mark alternative field, you should add `@JuriField`. Sometimes when you needs modeling your field on `fromUri()` you should added function with annotation `@JuriOnRestoreModel`. If you need more information, check this [test-code](src/test/kotlin/com/jaehochoe/juri/JuriTest.kt).

### Licencse

```
The MIT License (MIT)

Copyright (c) 2020 Jaeho Choe

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```



