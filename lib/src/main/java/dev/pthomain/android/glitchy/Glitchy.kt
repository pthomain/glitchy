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

package dev.pthomain.android.glitchy

import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.interceptor.Interceptors
import dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.interceptor.error.glitch.Glitch
import dev.pthomain.android.glitchy.interceptor.error.glitch.GlitchFactory
import dev.pthomain.android.glitchy.koin.GlitchyModule
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.dsl.koinApplication
import retrofit2.CallAdapter

object Glitchy {

    private fun defaultLogger() = object : Logger {
        override fun d(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, t: Throwable, message: String?) = Unit
    }

    private fun <E, M> getKoin(
        logger: Logger?,
        errorFactory: ErrorFactory<E>,
        returnTypeParser: ReturnTypeParser<M>?,
        interceptors: Interceptors<E>
    ) where E : Throwable, E : NetworkErrorPredicate =
        koinApplication {
            modules(
                GlitchyModule(
                    logger ?: defaultLogger(),
                    errorFactory,
                    returnTypeParser,
                    interceptors
                ).module
            )
        }.koin

    fun createGlitchCallAdapterFactory(
        interceptors: Interceptors<Glitch>? = null,
        logger: Logger? = null
    ) =
        createCallAdapterFactory<Glitch, Unit>(
            GlitchFactory(),
            null,
            interceptors,
            logger
        )

    fun <E, M> createCallAdapterFactory(
        errorFactory: ErrorFactory<E>,
        returnTypeParser: ReturnTypeParser<M>? = null,
        interceptors: Interceptors<E>? = null,
        logger: Logger? = null
    ) where E : Throwable, E : NetworkErrorPredicate =
        getKoin(
            logger,
            errorFactory,
            returnTypeParser,
            interceptors ?: Interceptors.None()
        ).get<CallAdapter.Factory>()

}