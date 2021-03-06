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

package dev.pthomain.android.glitchy.core.interceptor.builder

import dev.pthomain.android.boilerplate.core.builder.BaseExtendable
import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

class GlitchyBuilder<E, M, out F : InterceptorFactory<M>> internal constructor(
    private val errorFactory: ErrorFactory<E>,
    private val interceptorProvider: InterceptorProvider<M, F>,
    private val interceptors: Interceptors<M, F>,
    private val outcomePredicate: (M) -> Boolean
) : BaseExtendable<Module>()
        where E : Throwable,
              E : NetworkErrorPredicate {

    private var logger: Logger = object : Logger {
        override fun d(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, t: Throwable, message: String?) = Unit
    }

    fun withLogger(logger: Logger) = apply {
        this.logger = logger
    }

    override fun modules() = listOf(
        GlitchyModule(
            interceptorProvider,
            interceptors,
            errorFactory,
            outcomePredicate,
            logger
        ).module
    )

    fun build(): Glitchy<E, M, F> {
        val koin = koinApplication {
            modules(this@GlitchyBuilder.modules())
        }.koin

        return Glitchy(koin.get())
    }

}