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
import dev.pthomain.android.glitchy.core.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.Interceptors
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

class GlitchyBuilder<E> internal constructor(
    private val errorFactory: ErrorFactory<E>
) : BaseExtendable<Module>()
        where E : Throwable,
              E : NetworkErrorPredicate {

    private var logger: Logger = object : Logger {
        override fun d(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, t: Throwable, message: String?) = Unit
    }

    private var interceptError: Boolean = true
    private var interceptOutcome: Boolean = false
    private var interceptors: Interceptors = Interceptors.None()

    fun withLogger(logger: Logger) = apply {
        this.logger = logger
    }

    fun withInterceptError(interceptError: Boolean) = apply {
        this.interceptError = interceptError
    }

    fun withInterceptOutcome(interceptOutcome: Boolean) = apply {
        this.interceptOutcome = interceptOutcome
    }

    fun withInterceptors(interceptors: Interceptors) = apply {
        this.interceptors = interceptors
    }

    override fun modules() = listOf(
        GlitchyModule(
            interceptors,
            errorFactory,
            interceptError,
            interceptOutcome,
            logger
        ).module
    )

    fun build(): Glitchy<E> {
        val koin = koinApplication {
            modules(this@GlitchyBuilder.modules())
        }.koin

        return Glitchy(koin.get())
    }

}