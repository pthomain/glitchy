/*
 *
 *  Copyright (C) 2017-2020 Pierre Thomain
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package dev.pthomain.android.glitchy.retrofit.flow.adapter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

 class RetrofitFlowCallAdapterFactory : CallAdapter.Factory() {

     override fun get(
         returnType: Type,
         annotations: Array<Annotation>,
         retrofit: Retrofit
     ): CallAdapter<*, *>? {
         if (Flow::class.java != getRawType(returnType)) return null

         if (returnType !is ParameterizedType) throw IllegalArgumentException(
             "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>"
         )

         val responseType = getParameterUpperBound(0, returnType)
         val rawFlowType = getRawType(responseType)

         return if (rawFlowType == Response::class.java) {
             if (responseType !is ParameterizedType) throw IllegalArgumentException(
                 "Response must be parameterized as Response<Foo> or Response<out Foo>"
             )

             ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else BodyCallAdapter<Any>(responseType)
    }

    private class BodyCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Flow<T> {
            val deferred = CompletableDeferred<T>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        deferred.complete(response.body()!!)
                    } else {
                        deferred.completeExceptionally(HttpException(response))
                    }
                }
            })

            return flow {
                emit(deferred.await())
            }
        }
    }

    private class ResponseCallAdapter<T>(private val responseType: Type) :
        CallAdapter<T, Flow<Response<T>>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Flow<Response<T>> {
            val deferred = CompletableDeferred<Response<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    deferred.complete(response)
                }
            })

            return flow {
                emit(deferred.await())
            }
        }
    }

}