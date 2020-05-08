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

import dev.pthomain.android.glitchy.core.interceptor.builder.ExtensionBuilder
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.GlitchyRetrofit
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptors
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

class GlitchyRetrofitBuilder<E, M : Any> internal constructor() :
    ExtensionBuilder<GlitchyRetrofitBuilder<E, M>, GlitchyRetrofit<E>>
        where E : Throwable,
              E : NetworkErrorPredicate {

    private var returnTypeParser: ReturnTypeParser<M>? = null
    private var interceptors: RetrofitInterceptors<E> = RetrofitInterceptors.None()

    private var parentModules: List<Module>? = null

    override fun accept(modules: List<Module>) = apply {
        parentModules = modules
    }

    fun withReturnTypeParser(returnTypeParser: ReturnTypeParser<M>) = apply {
        this.returnTypeParser = returnTypeParser
    }

    fun withInterceptors(interceptors: RetrofitInterceptors<E>) = apply {
        this.interceptors = interceptors
    }

    override fun build(): GlitchyRetrofit<E> {
        val parentModules = this.parentModules
            ?: throw IllegalStateException("This builder needs to call GlitchyBuilder::extend")

        return koinApplication {
            modules(
                parentModules + GlitchyRetrofitModule(
                    returnTypeParser,
                    interceptors
                ).module
            )
        }.koin.run {
            GlitchyRetrofit(get())
        }
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