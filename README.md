Kotlin Coroutine Adapter
========================

A Retrofit 2 `CallAdapter.Factory` for [Kotlin coroutine's][1] `Deferred`.


Usage
-----

Add `CoroutineCallAdapterFactory` as a `Call` adapter when building your `Retrofit` instance:
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com/")
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()
```

Your service methods can now use `Deferred` as their return type.
```kotlin
interface MyService {
  @GET("/user")
  fun getUser(): Deferred<User>

  // or

  @GET("/user")
  fun getUser(): Deferred<Response<User>>
}
```


Download
--------

If you are using Kotlin 1.3, download [the latest JAR][2] or grab via [Maven][3]:
```xml
<dependency>
  <groupId>com.jakewharton.retrofit</groupId>
  <artifactId>retrofit2-kotlin-coroutines-adapter</artifactId>
  <version>0.9.0</version>
</dependency>
```
or [Gradle][3]:
```groovy
implementation 'com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.0'
```

If you are using Kotlin pre-1.3 and experimental coroutines, download [its latest JAR][4] or grab
via [Maven][5]:
```xml
<dependency>
  <groupId>com.jakewharton.retrofit</groupId>
  <artifactId>retrofit2-kotlin-coroutines-experimental-adapter</artifactId>
  <version>1.0.0</version>
</dependency>
```
or [Gradle][5]:
```groovy
implementation 'com.jakewharton.retrofit:retrofit2-kotlin-coroutines-experimental-adapter:1.0.0'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].


License
=======

    Copyright 2017 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




 [1]: https://kotlinlang.org/docs/reference/coroutines.html
 [2]: https://search.maven.org/remote_content?g=com.jakewharton.retrofit&a=retrofit2-kotlin-coroutines-adapter&v=LATEST
 [3]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.jakewharton.retrofit%22%20a%3A%22retrofit2-kotlin-coroutines-adapter%22
 [4]: https://search.maven.org/remote_content?g=com.jakewharton.retrofit&a=retrofit2-kotlin-coroutines-experimental-adapter&v=LATEST
 [5]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.jakewharton.retrofit%22%20a%3A%22retrofit2-kotlin-coroutines-experimental-adapter%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
