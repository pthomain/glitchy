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

import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitCompositeInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptors
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitOutcomeInterceptor
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

internal class GlitchyRetrofitModule<E, M : Any>(
    returnTypeParser: ReturnTypeParser<M>?,
    interceptors: RetrofitInterceptors<E>
) where E : Throwable,
        E : NetworkErrorPredicate {

    val module = module {

        single {
            RetrofitCompositeInterceptor.Factory(
                interceptors,
                get(),
                RetrofitOutcomeInterceptor.Factory(get())
            )
        }

        single { RxJava2CallAdapterFactory.create() }

        single<CallAdapter.Factory> {
            RetrofitCallAdapterFactory<E, M>(
                get(),
                get(),
                returnTypeParser,
                get()
            )
        }

    }
}