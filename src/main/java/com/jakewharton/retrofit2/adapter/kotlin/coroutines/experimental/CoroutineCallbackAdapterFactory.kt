package com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental

import retrofit2.Call
import retrofit2.Callback
import retrofit2.CallbackAdapter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED

class CoroutineCallbackAdapterFactory private constructor() : CallbackAdapter.Factory() {
  companion object {
    @JvmStatic @JvmName("create")
    operator fun invoke() = CoroutineCallbackAdapterFactory()
  }

  override fun get(
      parameterType: Type,
      returnType: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit
  ): CallbackAdapter<*, *>? {
    if (Continuation::class.java != getRawType(parameterType)) {
      return null
    }
    if (Any::class.java != returnType) {
      return null
    }
    if (parameterType !is ParameterizedType) {
      throw AssertionError("Continuation type must be parameterized. Report this as a bug.")
    }
    val responseType = getParameterLowerBound(0, parameterType)

    val rawResponseType = getRawType(responseType)
    return if (rawResponseType == Response::class.java) {
      if (responseType !is ParameterizedType) {
        throw IllegalStateException("Response must be parameterized as Response<Foo> or Response<out Foo>")
      }
      ResponseCallbackAdapter<Any>(getParameterUpperBound(0, responseType))
    } else {
      BodyCallbackAdapter<Any>(responseType)
    }
  }

  private class BodyCallbackAdapter<T>(
      private val responseType: Type
  ) : CallbackAdapter<T, Continuation<T>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<T>, continuation: Continuation<T>): Any? {
      call.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
          if (response.isSuccessful) {
            continuation.resume(response.body()!!)
          } else {
            continuation.resumeWithException(HttpException(response))
          }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
          continuation.resumeWithException(t)
        }
      })
      return COROUTINE_SUSPENDED
    }
  }

  private class ResponseCallbackAdapter<T>(
      private val responseType: Type
  ) : CallbackAdapter<T, Continuation<Response<T>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<T>, continuation: Continuation<Response<T>>): Any? {
      call.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
          continuation.resume(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
          continuation.resumeWithException(t)
        }
      })
      return COROUTINE_SUSPENDED
    }
  }
}
