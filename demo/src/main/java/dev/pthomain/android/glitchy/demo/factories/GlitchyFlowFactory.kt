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

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.asFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.demo.api.error.ApiError
import dev.pthomain.android.glitchy.demo.api.getRetrofit
import dev.pthomain.android.glitchy.demo.interceptors.FlowLoggingInterceptor
import dev.pthomain.android.glitchy.demo.throwHandledException
import dev.pthomain.android.glitchy.demo.throwUnhandledException
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptor
import dev.pthomain.android.glitchy.retrofit.error.RetrofitGlitchFactory
import dev.pthomain.android.glitchy.retrofit.flow.GlitchyRetrofitFlow
import dev.pthomain.android.glitchy.retrofit.flow.RetrofitFlowInterceptors
import dev.pthomain.android.glitchy.retrofit.interceptors.asRetrofitFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

private val exceptionFlowInterceptor = object : FlowInterceptor() {
    override fun flatMap(upstream: Flow<Any>) = when {
        throwHandledException.getAndSet(false) -> flow<Nothing> {
            throw IOException("Some IO exception occurred")
        }

        throwUnhandledException.getAndSet(false) -> flow<Nothing> {
            throw NullPointerException("Something was null")
        }

        else -> upstream
    }
}

private val flowInterceptors = RetrofitFlowInterceptors.around(
    listOf(exceptionFlowInterceptor.asFactory()),
    listOf(asRetrofitFactory(::FlowLoggingInterceptor))
)

private fun <E> getFlowCallAdapterFactory(errorFactory: ErrorFactory<E>)
        where E : Throwable,
              E : NetworkErrorPredicate =
    GlitchyRetrofitFlow.Default.builder(
        errorFactory,
        flowInterceptors
    ).build().callAdapterFactory

val glitchRetrofitFlow = getRetrofit(
    getFlowCallAdapterFactory(RetrofitGlitchFactory())
)

val apiErrorRetrofitFlow = getRetrofit(
    getFlowCallAdapterFactory(ApiError.Factory())
)