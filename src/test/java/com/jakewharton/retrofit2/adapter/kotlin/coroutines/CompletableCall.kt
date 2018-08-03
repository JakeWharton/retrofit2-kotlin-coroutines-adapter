package com.jakewharton.retrofit2.adapter.kotlin.coroutines

import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InterruptedIOException
import java.util.concurrent.CountDownLatch

private val DUMMY_REQUEST = Request.Builder()
    .url("http://example.com")
    .build()

class CompletableCall<T>(private val request: Request = DUMMY_REQUEST) : Call<T> {
  private val lock = Any()
  private var executed = false
  private var canceled = false
  private var callback: Callback<T>? = null
  private val done = CountDownLatch(1)
  private var exception: Throwable? = null
  private var response: Response<T>? = null

  fun completeWithException(t: Throwable) {
    synchronized(lock) {
      exception = t
      callback?.onFailure(this, t)
    }
    done.countDown()
  }

  fun complete(body: T) = complete(Response.success(body))

  fun complete(response: Response<T>) {
    synchronized(lock) {
      this.response = response
      callback?.onResponse(this, response)
    }
    done.countDown()
  }

  override fun request() = request
  override fun isCanceled() = synchronized(lock) { canceled }
  override fun isExecuted() = synchronized(lock) { executed }
  override fun clone() = CompletableCall<T>(request)

  override fun cancel() {
    synchronized(lock) {
      if (canceled) return
      canceled = true

      val exception = InterruptedIOException("canceled")
      this.exception = exception
      callback?.onFailure(this, exception)
    }
    done.countDown()
  }

  override fun execute(): Response<T> {
    synchronized(lock) {
      check(!executed) { "Already executed "}
      executed = true
    }
    done.await()
    synchronized(lock) {
      if (exception != null) {
        throw exception!!
      }
      return response!!
    }
  }

  override fun enqueue(callback: Callback<T>) {
    synchronized(lock) {
      if (exception != null) {
        callback.onFailure(this, exception!!)
      } else if (response != null) {
        callback.onResponse(this, response!!)
      } else {
        this.callback = callback
      }
    }
  }
}
