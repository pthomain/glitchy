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

import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyBuilder
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.flow.GlitchyFlow
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptors
import dev.pthomain.android.glitchy.retrofit.flow.adapter.RetrofitFlowCallAdapterFactory
import dev.pthomain.android.glitchy.retrofit.flow.type.FlowReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import retrofit2.CallAdapter

class GlitchyRetrofitFlow internal constructor(
    val callAdapterFactory: CallAdapter.Factory
) {

    object Custom {
        fun <E, M> extension(
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            GlitchyRetrofitFlowBuilder<E, M>(
                defaultCallAdapterFactory,
                returnTypeParser
            )

        fun <E, M> builder(
            glitchyBuilder: GlitchyBuilder<E, M, InterceptorFactory<M>>,
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
            interceptors: FlowInterceptors<M>,
            defaultCallAdapterFactory: CallAdapter.Factory,
            returnTypeParser: ReturnTypeParser<M>
        ) where E : Throwable,
                E : NetworkErrorPredicate =
            builder(
                GlitchyFlow.builder(errorFactory, interceptors),
                defaultCallAdapterFactory,
                returnTypeParser
            )
    }

    object Default {
        fun <E> extension(): GlitchyRetrofitFlowBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            Custom.extension(
                RetrofitFlowCallAdapterFactory(),
                returnTypeParser
            )

        fun <E> builder(glitchyBuilder: GlitchyBuilder<E, Unit, InterceptorFactory<Unit>>)
                : GlitchyRetrofitFlowBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(extension())

        fun <E> builder(
            errorFactory: ErrorFactory<E>,
            interceptors: FlowInterceptors<Unit>
        ): GlitchyRetrofitFlowBuilder<E, Unit>
                where E : Throwable,
                      E : NetworkErrorPredicate {
            return Custom.builder(
                errorFactory,
                interceptors,
                RetrofitFlowCallAdapterFactory(),
                returnTypeParser
            )
        }

        private val returnTypeParser = OutcomeReturnTypeParser.getDefaultInstance(
            FlowReturnTypeParser.DEFAULT
        )

    }

}
