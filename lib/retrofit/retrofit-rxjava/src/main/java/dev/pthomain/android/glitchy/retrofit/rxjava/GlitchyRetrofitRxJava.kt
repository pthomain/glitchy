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

import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyBuilder
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.rxjava.type.RxReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import dev.pthomain.android.glitchy.rxjava.GlitchyRxJava
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptors
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class GlitchyRetrofitRxJava internal constructor(
    val callAdapterFactory: CallAdapter.Factory
) {

    object Custom {
        fun <E, M> extension(
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitRxJavaBuilder<E, M>(
                defaultCallAdapterFactory,
                returnTypeParser
            )

        fun <E, M> builder(
            glitchyBuilder: GlitchyBuilder<E, RetrofitMetadata<M>, RetrofitInterceptor.Factory<M>>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            glitchyBuilder.extend(
                extension<E, M>(
                    defaultCallAdapterFactory,
                    returnTypeParser
                )
            )

        fun <E, M> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: RxInterceptors<RetrofitMetadata<M>, RetrofitInterceptor.Factory<M>>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            builder(
                GlitchyRxJava.builder(errorFactory, interceptors),
                defaultCallAdapterFactory,
                returnTypeParser
            )

    }

    object Default {

        fun <E> extension(isAsync: Boolean = true): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            Custom.extension(
                getRxJava2CallAdapterFactory(isAsync),
                returnTypeParser
            )

        fun <E> builder(
            glitchyBuilder: GlitchyBuilder<E, RetrofitMetadata<Unit>, RetrofitInterceptor.Factory<Unit>>,
            isAsync: Boolean = true
        ): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(extension(isAsync))

        fun <E> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: RxInterceptors<RetrofitMetadata<Unit>, RetrofitInterceptor.Factory<Unit>>,
            isAsync: Boolean = true
        ): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            Custom.builder(
                errorFactory,
                interceptors,
                getRxJava2CallAdapterFactory(isAsync),
                returnTypeParser
            )

        private fun getRxJava2CallAdapterFactory(isAsync: Boolean) =
            if (isAsync) RxJava2CallAdapterFactory.createAsync()
            else RxJava2CallAdapterFactory.create()

        private val returnTypeParser = OutcomeReturnTypeParser.getDefaultInstance(
            RxReturnTypeParser.DEFAULT
        )
    }
}

