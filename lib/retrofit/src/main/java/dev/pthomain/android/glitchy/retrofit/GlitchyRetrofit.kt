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

package dev.pthomain.android.glitchy.retrofit

import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.builder.GlitchyBuilder
import dev.pthomain.android.glitchy.core.interceptor.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.builder.GlitchyRetrofitBuilder
import retrofit2.CallAdapter

class GlitchyRetrofit<E>(
    val callAdapterFactory: CallAdapter.Factory
) where E : Throwable,
        E : NetworkErrorPredicate {

    companion object {

        fun <E, M : Any> extension(): GlitchyRetrofitBuilder<E, M>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            GlitchyRetrofitBuilder()

        fun <E, M : Any> builder(glitchyBuilder: GlitchyBuilder<E>): GlitchyRetrofitBuilder<E, M>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(extension<E, M>())

        fun <E, M : Any> builder(errorFactory: ErrorFactory<E>): GlitchyRetrofitBuilder<E, M>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            builder(Glitchy.builder(errorFactory))

        fun <E> defaultExtension(): GlitchyRetrofitBuilder<E, Any>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            GlitchyRetrofitBuilder.defaultBuilder()

        fun <E> defaultBuilder(glitchyBuilder: GlitchyBuilder<E>): GlitchyRetrofitBuilder<E, Any>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            glitchyBuilder.extend(defaultExtension<E>())

        fun <E> defaultBuilder(errorFactory: ErrorFactory<E>): GlitchyRetrofitBuilder<E, Any>
                where E : Throwable,
                      E : NetworkErrorPredicate =
            defaultBuilder(Glitchy.builder(errorFactory))

    }

}