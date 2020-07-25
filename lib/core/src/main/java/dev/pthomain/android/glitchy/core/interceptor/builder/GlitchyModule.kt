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
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.CompositeInterceptor
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal class GlitchyModule<E, M>(
    private val interceptorProvider: InterceptorProvider<>,
    private val errorFactory: ErrorFactory<E>,
    private val asOutcome: Boolean,
    private val logger: Logger
) where E : Throwable,
        E : NetworkErrorPredicate {

    val module = module {
        single { logger }

        single { errorFactory }

        single(named(ERROR)) { interceptorProvider.errorInterceptor }

        single(named(OUTCOME)) { interceptorProvider.outcomeInterceptor }

        single {
            CompositeInterceptor<E, M, *>(
                interceptorProvider.interceptors,
                get(named(ERROR)),
                if (asOutcome) get(named(OUTCOME)) else null
            )
        }
    }
}

internal const val ERROR = "ERROR"
internal const val OUTCOME = "OUTCOME"