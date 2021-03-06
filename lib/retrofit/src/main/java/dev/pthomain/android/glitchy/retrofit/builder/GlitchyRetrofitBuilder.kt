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

package dev.pthomain.android.glitchy.retrofit.builder

import dev.pthomain.android.boilerplate.core.builder.BaseExtensionBuilder
import dev.pthomain.android.boilerplate.core.builder.Extendable
import dev.pthomain.android.boilerplate.core.builder.ExtensionBuilder
import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.builder.InterceptorProvider
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptorFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import retrofit2.CallAdapter

abstract class BaseGlitchyRetrofitBuilder<E, M : Any, B : BaseGlitchyRetrofitBuilder<E, M, B, R>, R>(
    errorFactory: ErrorFactory<E>,
    private val returnTypeParser: OutcomeReturnTypeParser<M>,
    private val defaultCallAdapterFactory: CallAdapter.Factory,
    interceptors: Interceptors<RetrofitMetadata<M>, InterceptorFactory<RetrofitMetadata<M>>>,
    interceptorProvider: InterceptorProvider<RetrofitMetadata<M>, RetrofitInterceptorFactory<M>>
) : BaseExtensionBuilder<R, Module, B>(),
    Extendable<Module>
        where E : Throwable,
              E : NetworkErrorPredicate {

    init {
        @Suppress("UNCHECKED_CAST")
        Glitchy.builder(
            errorFactory,
            interceptorProvider,
            interceptors,
            { returnTypeParser.outcomePredicate(it.parsedType.typeToken) }
        ).extend(this as B)
    }

    override fun buildInternal(parentModules: List<Module>): R =
        koinApplication {
            modules(parentModules + this@BaseGlitchyRetrofitBuilder.modules())
        }.koin.run {
            getInstance(get())
        }

    protected abstract fun getInstance(callAdapterFactory: CallAdapter.Factory): R

    private fun modules() = listOf(
        GlitchyRetrofitModule<E, M>(
            returnTypeParser,
            defaultCallAdapterFactory
        ).module
    )

    override fun <B, EB : ExtensionBuilder<B, Module, EB>> extend(extensionBuilder: EB) =
        extensionBuilder.accept(modules())

}