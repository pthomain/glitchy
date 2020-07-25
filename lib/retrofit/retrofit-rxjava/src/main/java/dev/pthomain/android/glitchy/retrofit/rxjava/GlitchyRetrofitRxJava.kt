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

import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyBuilder
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptors
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

class GlitchyRetrofitRxJava internal constructor(
    val callAdapterFactory: CallAdapter.Factory
) {

    companion object {
        fun <E, M> extension(
            defaultCallAdapterFactory: CallAdapter.Factory,
            metadataResolver: (Type) -> M
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitRxJavaBuilder<E, M>(
                metadataResolver,
                defaultCallAdapterFactory
            )

        fun <E, M> builder(
            glitchyBuilder: GlitchyBuilder<E>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            metadataResolver: (Type) -> M
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            glitchyBuilder.extend(
                extension<E, M>(
                    defaultCallAdapterFactory,
                    metadataResolver
                )
            )

        fun <E, M> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: RxInterceptors<RetrofitMetadata<Unit>, RetrofitInterceptor.Factory<Unit>>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            metadataResolver: (Type) -> M
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            builder(
                Glitchy.builder(errorFactory, interceptors),
                defaultCallAdapterFactory,
                metadataResolver
            )

        fun <E> defaultExtension(isAsync: Boolean = true): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            extension(
                if (isAsync) RxJava2CallAdapterFactory.createAsync()
                else RxJava2CallAdapterFactory.create()
            ) { Unit }

        fun <E> defaultBuilder(
            glitchyBuilder: GlitchyBuilder<E>,
            isAsync: Boolean = true
        ): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(defaultExtension(isAsync))

        fun <E> defaultBuilder(
            errorFactory: ErrorFactory<E>,
            interceptors: RxInterceptors<RetrofitMetadata<Unit>, RetrofitInterceptor.Factory<Unit>>,
            isAsync: Boolean = true
        ): GlitchyRetrofitRxJavaBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            builder(
                errorFactory,
                interceptors,
                if (isAsync) RxJava2CallAdapterFactory.createAsync()
                else RxJava2CallAdapterFactory.create()
            ) { Unit }

    }

}
