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
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.GlitchyRetrofit
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptors
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

class GlitchyRetrofitBuilder<E, M : Any> internal constructor() :
    BaseExtensionBuilder<GlitchyRetrofit<E>, Module, GlitchyRetrofitBuilder<E, M>>()
        where E : Throwable,
              E : NetworkErrorPredicate {

    private var returnTypeParser: ReturnTypeParser<M>? = null
    private var interceptors: RetrofitInterceptors<E> = RetrofitInterceptors.None()

    fun withReturnTypeParser(returnTypeParser: ReturnTypeParser<M>) = apply {
        this.returnTypeParser = returnTypeParser
    }

    fun withInterceptors(interceptors: RetrofitInterceptors<E>) = apply {
        this.interceptors = interceptors
    }

    override fun buildInternal(parentModules: List<Module>) =
        koinApplication {
            modules(
                parentModules + GlitchyRetrofitModule(
                    returnTypeParser,
                    interceptors
                ).module
            )
        }.koin.run {
            GlitchyRetrofit<E>(get())
        }

    companion object {
        fun <E> defaultBuilder()
                where E : Throwable,
                      E : NetworkErrorPredicate =
            GlitchyRetrofitBuilder<E, Any>().apply {
                withReturnTypeParser(OutcomeReturnTypeParser.INSTANCE)
            }
    }
}