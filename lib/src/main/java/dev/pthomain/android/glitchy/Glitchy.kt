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
import dev.pthomain.android.glitchy.interceptor.RetrofitInterceptor
import dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.interceptor.error.glitch.Glitch
import dev.pthomain.android.glitchy.interceptor.error.glitch.GlitchFactory
import dev.pthomain.android.glitchy.koin.GlitchyModule
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import retrofit2.CallAdapter
import java.lang.reflect.Type

object Glitchy {

    private var koin: Koin? = null

    private fun defaultLogger() = object : Logger {
        override fun d(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, message: String) = Unit
        override fun e(tagOrCaller: Any, t: Throwable, message: String?) = Unit
    }

    private fun defaultTypeParser() = object : ReturnTypeParser<Unit> {
        override fun parseReturnType(returnType: Type) = object : ParsedType<Unit> {
            override val metadata = Unit
            override val returnType = returnType
            override val responseClass: Class<*> = returnType as Class<*>
        }
    }

    @Synchronized
    private fun <E, M> getKoin(
        logger: Logger?,
        errorFactory: ErrorFactory<E>,
        returnTypeParser: ReturnTypeParser<M>,
        interceptorFactoryList: List<RetrofitInterceptor.Factory<E>>
    ): Koin where E : Throwable, E : NetworkErrorPredicate {
        if (koin != null) stopKoin()

        koin = startKoin {
            modules(
                GlitchyModule(
                    logger ?: defaultLogger(),
                    errorFactory,
                    returnTypeParser,
                    interceptorFactoryList
                ).module
            )
        }.koin

        return koin!!
    }

    fun createCallAdapterFactory(
        interceptorFactoryList: List<RetrofitInterceptor.Factory<Glitch>> = emptyList(),
        logger: Logger? = null
    ) =
        createCallAdapterFactory(
            GlitchFactory(),
            defaultTypeParser(),
            interceptorFactoryList,
            logger
        )

    fun <E> createCallAdapterFactory(
        errorFactory: ErrorFactory<E>,
        interceptorFactoryList: List<RetrofitInterceptor.Factory<E>> = emptyList(),
        logger: Logger? = null
    ) where E : Throwable, E : NetworkErrorPredicate =
        createCallAdapterFactory(
            errorFactory,
            defaultTypeParser(),
            interceptorFactoryList,
            logger
        )

    fun <M, E> createCallAdapterFactory(
        errorFactory: ErrorFactory<E>,
        returnTypeParser: ReturnTypeParser<M>,
        interceptorFactoryList: List<RetrofitInterceptor.Factory<E>> = emptyList(),
        logger: Logger? = null
    ) where E : Throwable, E : NetworkErrorPredicate =
        getKoin(
            logger,
            errorFactory,
            returnTypeParser,
            interceptorFactoryList
        ).get<CallAdapter.Factory>()

}