@file:Suppress("UNCHECKED_CAST")

package com.jakewharton.retrofit2.adapter.kotlin.coroutines

import com.google.common.reflect.TypeToken
import kotlinx.coroutines.Deferred
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class CancelTest {
  private val factory = CoroutineCallAdapterFactory()
  private val retrofit = Retrofit.Builder()
      .baseUrl("http://example.com")
      .callFactory { TODO() }
      .build()

  @Test fun noCancelOnResponse() {
    val deferredString = typeOf<Deferred<String>>()
    val adapter = factory.get(deferredString, emptyArray(), retrofit)!! as CallAdapter<String, Deferred<String>>
    val call = CompletableCall<String>()
    val deferred = adapter.adapt(call)
    call.complete("hey")
    assertFalse(call.isCanceled)
    assertTrue(deferred.isCompleted)
  }

  @Test fun noCancelOnError() {
    val deferredString = typeOf<Deferred<String>>()
    val adapter = factory.get(deferredString, emptyArray(), retrofit)!! as CallAdapter<String, Deferred<String>>
    val call = CompletableCall<String>()
    val deferred = adapter.adapt(call)
    call.completeWithException(IOException())
    assertFalse(call.isCanceled)
    assertTrue(deferred.isCompletedExceptionally)
  }

  @Test fun cancelOnCancel() {
    val deferredString = typeOf<Deferred<String>>()
    val adapter = factory.get(deferredString, emptyArray(), retrofit)!! as CallAdapter<String, Deferred<String>>
    val call = CompletableCall<String>()
    val deferred = adapter.adapt(call)
    assertFalse(call.isCanceled)
    deferred.cancel()
    assertTrue(call.isCanceled)
  }

  private inline fun <reified T> typeOf(): Type = object : TypeToken<T>() {}.type
}
