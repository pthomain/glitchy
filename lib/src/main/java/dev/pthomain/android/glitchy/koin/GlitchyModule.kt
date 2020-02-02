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

package dev.pthomain.android.glitchy.koin

import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.interceptor.CompositeInterceptor
import dev.pthomain.android.glitchy.interceptor.RetrofitInterceptor
import dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.RetrofitCallAdapter
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

internal class GlitchyModule<E, M>(
    private val logger: Logger,
    private val errorFactory: ErrorFactory<E>,
    private val returnTypeParser: ReturnTypeParser<M>,
    private val interceptorFactoryList: List<RetrofitInterceptor.Factory<E>>
) where E : Throwable,
        E : NetworkErrorPredicate {

    val module = module {

        single { errorFactory }

        single { CompositeInterceptor.Factory(interceptorFactoryList) }

        single { RxJava2CallAdapterFactory.create() }

        single<CallAdapter.Factory> {
            RetrofitCallAdapter.Factory<E, M>(
                get(),
                get(),
                returnTypeParser,
                logger
            )
        }

    }
}