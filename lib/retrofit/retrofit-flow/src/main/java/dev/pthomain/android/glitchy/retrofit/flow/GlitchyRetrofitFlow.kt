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

package dev.pthomain.android.glitchy.retrofit.flow

import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyBuilder
import dev.pthomain.android.glitchy.core.interceptor.builder.InterceptorProvider
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.flow.GlitchyFlow
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptors
import dev.pthomain.android.glitchy.retrofit.flow.adapter.RetrofitFlowCallAdapterFactory
import retrofit2.CallAdapter
import java.lang.reflect.Type

class GlitchyRetrofitFlow internal constructor(
    val callAdapterFactory: CallAdapter.Factory
) {

    companion object {
        fun <E, M> extension(
            defaultCallAdapterFactory: CallAdapter.Factory,
            metadataResolver: (Type) -> M
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitFlowBuilder<E, M>(
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

        fun <E, M, F : InterceptorFactory<M>> builder(
            errorFactory: ErrorFactory<E>,
            interceptorProvider: InterceptorProvider<M, F>,
            metadataResolver: (Type) -> M,
            defaultCallAdapterFactory: CallAdapter.Factory
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            builder(
                Glitchy.builder(errorFactory, interceptorProvider),
                defaultCallAdapterFactory,
                metadataResolver
            )

        fun <E> defaultExtension(): GlitchyRetrofitFlowBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            extension(RetrofitFlowCallAdapterFactory()) { Unit }

        fun <E> defaultBuilder(glitchyBuilder: GlitchyBuilder<E>): GlitchyRetrofitFlowBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(defaultExtension())

        fun <E> defaultBuilder(
            errorFactory: ErrorFactory<E>,
            interceptors: FlowInterceptors<Unit>
        ): GlitchyRetrofitFlowBuilder<E, Any>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            builder(
                Glitchy.builder(
                    errorFactory,
                    GlitchyFlow.getInterceptorProvider(errorFactory, interceptors)
                )
            )

    }

}
