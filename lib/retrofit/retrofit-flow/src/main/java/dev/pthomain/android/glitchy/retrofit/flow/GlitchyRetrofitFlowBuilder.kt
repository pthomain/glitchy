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

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.flow.GlitchyFlow
import dev.pthomain.android.glitchy.retrofit.builder.BaseGlitchyRetrofitBuilder
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import retrofit2.CallAdapter

class GlitchyRetrofitFlowBuilder<E, M : Any> internal constructor(
    errorFactory: ErrorFactory<E>,
    defaultCallAdapterFactory: CallAdapter.Factory,
    returnTypeParser: OutcomeReturnTypeParser<M>,
    interceptors: Interceptors<RetrofitMetadata<M>, InterceptorFactory<RetrofitMetadata<M>>>
) : BaseGlitchyRetrofitBuilder<E, M, GlitchyRetrofitFlowBuilder<E, M>, GlitchyRetrofitFlow>(
    errorFactory,
    returnTypeParser,
    defaultCallAdapterFactory,
    interceptors,
    GlitchyFlow.interceptorProvider(errorFactory)
) where E : Throwable,
        E : NetworkErrorPredicate {

    override fun getInstance(callAdapterFactory: CallAdapter.Factory) =
        GlitchyRetrofitFlow(callAdapterFactory)

}