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

package dev.pthomain.android.glitchy.demo

import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptors
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptor.CombinedRxInterceptor
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

object GlitchyFactory {

    val throwHandledException = AtomicBoolean(false)
    val throwUnhandledException = AtomicBoolean(false)

    private val exceptionRxInterceptor = object : CombinedRxInterceptor() {
        override fun apply(upstream: Observable<Any>) = when {
            throwHandledException.getAndSet(false) -> Observable.error<Any>(
                IOException("Some IO exception occurred")
            )

            throwUnhandledException.getAndSet(false) -> Observable.error<Any>(
                NullPointerException("Something was null")
            )

            else -> upstream
        }
    }

    private val exceptionFlowInterceptor = object : FlowInterceptor() {
        override fun interceptFlow(upstream: Flow<*>) = when {
            throwHandledException.getAndSet(false) -> flow<Nothing> {
                throw IOException("Some IO exception occurred")
            }

            throwUnhandledException.getAndSet(false) -> flow<Nothing> {
                throw NullPointerException("Something was null")
            }

            else -> upstream
        }
    }

    fun <E> getRxInterceptors()
            where E : Throwable,
                  E : NetworkErrorPredicate = RetrofitInterceptors.Before(
        object : RetrofitInterceptor.Factory<E> {
            override fun <M> create(
                parsedType: ParsedType<M>,
                call: Call<Any>
            ) = exceptionRxInterceptor
        }
    )

    fun <E> getFlowInterceptors()
            where E : Throwable,
                  E : NetworkErrorPredicate = RetrofitInterceptors.Before(
        object : RetrofitInterceptor.Factory<E> {
            override fun <M> create(
                parsedType: ParsedType<M>,
                call: Call<Any>
            ) = exceptionFlowInterceptor
        }
    )

}