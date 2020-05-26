# JUri

JUri is Java/kotlin library that can be used to convert Java/Kotlin Object into their URI representation. It can also be used to convert a URI string to an equivalent Java/Kotlin object. JUri can work with arbitrary Java objects including pre-existing objects that you added a little annotations. 

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

```kotlin
@JuriModel(scheme = "myapp", host = "search")
data class SearchOptions(
    val query: String,
    val sort: String?,
    val page: Int
)

val model = SearchOptions("paris", "price:desc", 1)
println(Juri.toUri(model)) // result is myapp://search?query=paris&sort=price:desc&page=1

val model = Juri.fromUri("myapp://search?query=paris&sort=price:desc&page=1", SearchOptions::class.java)
println(model) // result is SearchOptions(query=paris, sort=price:desc, page=1)        
```



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



