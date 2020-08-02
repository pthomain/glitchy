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

package dev.pthomain.android.glitchy.demo.factories

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.demo.api.error.ApiError
import dev.pthomain.android.glitchy.demo.api.getRetrofit
import dev.pthomain.android.glitchy.demo.throwHandledException
import dev.pthomain.android.glitchy.demo.throwUnhandledException
import dev.pthomain.android.glitchy.retrofit.error.RetrofitGlitchFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.rxjava.GlitchyRetrofitRxJava
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser.Companion.OutcomeToken
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptor
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptors
import io.reactivex.Observable
import java.io.IOException

private val exceptionRxInterceptor = object : RxInterceptor.CombinedRxInterceptor() {
    override fun apply(upstream: Observable<Any>) = when {
        throwHandledException.getAndSet(false) -> Observable.error(
            IOException("Some IO exception occurred")
        )

        throwUnhandledException.getAndSet(false) -> Observable.error(
            NullPointerException("Something was null")
        )

        else -> upstream
    }
}

private val rxInterceptors = RxInterceptors.Before(
    object : InterceptorFactory<RetrofitMetadata<OutcomeToken>> {
        override fun create(metadata: RetrofitMetadata<OutcomeToken>?) = exceptionRxInterceptor
    }
)

private fun <E> getRxJavaCallAdapterFactory(errorFactory: ErrorFactory<E>)
        where E : Throwable,
              E : NetworkErrorPredicate =
    GlitchyRetrofitRxJava.Default.builder(
        errorFactory,
        rxInterceptors
    ).build().callAdapterFactory

 val glitchRetrofitRxJava = getRetrofit(
    getRxJavaCallAdapterFactory(RetrofitGlitchFactory())
)

 val apiErrorRetrofitRxJava = getRetrofit(
    getRxJavaCallAdapterFactory(ApiError.Factory())
)