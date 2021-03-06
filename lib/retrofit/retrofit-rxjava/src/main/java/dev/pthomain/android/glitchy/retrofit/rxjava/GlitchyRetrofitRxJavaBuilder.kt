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
import dev.pthomain.android.glitchy.retrofit.builder.BaseGlitchyRetrofitBuilder
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptorFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser
import dev.pthomain.android.glitchy.rxjava.GlitchyRxJava
import retrofit2.CallAdapter

class GlitchyRetrofitRxJavaBuilder<E, M : Any> internal constructor(
    errorFactory: ErrorFactory<E>,
    defaultCallAdapterFactory: CallAdapter.Factory,
    returnTypeParser: OutcomeReturnTypeParser<M>,
    interceptors: Interceptors<RetrofitMetadata<M>, RetrofitInterceptorFactory<M>>
) : BaseGlitchyRetrofitBuilder<E, M, GlitchyRetrofitRxJavaBuilder<E, M>, GlitchyRetrofitRxJava>(
    errorFactory,
    returnTypeParser,
    defaultCallAdapterFactory,
    interceptors,
    GlitchyRxJava.interceptorProvider(errorFactory)
) where E : Throwable,
        E : NetworkErrorPredicate {

    override fun getInstance(callAdapterFactory: CallAdapter.Factory) =
        GlitchyRetrofitRxJava(callAdapterFactory)

}