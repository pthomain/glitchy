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

package dev.pthomain.android.glitchy.retrofit.rxjava

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptorFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.rxjava.type.RxReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser.Companion.OutcomeToken
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class GlitchyRetrofitRxJava internal constructor(
    val callAdapterFactory: CallAdapter.Factory
) {

    object Custom {

        fun <E, M> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: Interceptors<RetrofitMetadata<M>, RetrofitInterceptorFactory<M>>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitRxJavaBuilder(
                errorFactory,
                defaultCallAdapterFactory,
                returnTypeParser,
                interceptors
            )

    }

    object Default {

        fun <E> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: Interceptors<RetrofitMetadata<OutcomeToken>, RetrofitInterceptorFactory<OutcomeToken>>,
            isAsync: Boolean = true
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitRxJavaBuilder(
                errorFactory,
                getRxJava2CallAdapterFactory(isAsync),
                returnTypeParser,
                interceptors
            )

        private fun getRxJava2CallAdapterFactory(isAsync: Boolean) =
            if (isAsync) RxJava2CallAdapterFactory.createAsync()
            else RxJava2CallAdapterFactory.create()

        private val returnTypeParser = OutcomeReturnTypeParser.getDefaultInstance(
            RxReturnTypeParser.DEFAULT
        )
    }
}

