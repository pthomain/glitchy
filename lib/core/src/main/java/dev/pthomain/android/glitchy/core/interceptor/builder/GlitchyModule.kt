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

import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyInterceptor.*
import dev.pthomain.android.glitchy.core.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.error.ErrorInterceptor
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.CompositeInterceptor
import dev.pthomain.android.glitchy.core.interceptor.interceptors.Interceptor
import dev.pthomain.android.glitchy.core.interceptor.interceptors.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.outcome.OutcomeInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal class GlitchyModule<E>(
    private val interceptors: Interceptors,
    private val errorFactory: ErrorFactory<E>,
    private val interceptError: Boolean,
    private val interceptOutcome: Boolean,
    private val logger: Logger
) where E : Throwable,
        E : NetworkErrorPredicate {

    val module = module {
        single { logger }

        single { errorFactory }

        single { ErrorInterceptor(get<ErrorFactory<E>>()) }

        single { OutcomeInterceptor(get<ErrorFactory<E>>()) }

        single<Interceptor>(named(ERROR)) { get<ErrorInterceptor<E>>() }

        single<Interceptor>(named(OUTCOME)) { get<OutcomeInterceptor<E>>() }

        single<Interceptor>(named(COMPOSITE)) {
            CompositeInterceptor<E>(
                interceptors,
                if (interceptError) get<ErrorInterceptor<E>>() else null,
                if (interceptOutcome) get<OutcomeInterceptor<E>>() else null
            )
        }
    }

}