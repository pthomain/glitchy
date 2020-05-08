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

package dev.pthomain.android.glitchy.retrofit.interceptors

import dev.pthomain.android.glitchy.core.interceptor.error.ErrorInterceptor
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.BaseCompositeInterceptor
import dev.pthomain.android.glitchy.core.interceptor.interceptors.Interceptor
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import retrofit2.Call

class RetrofitCompositeInterceptor<E, M> private constructor(
    private val interceptors: RetrofitInterceptors<E>,
    private val errorInterceptor: Interceptor,
    private val outcomeInterceptorFactory: RetrofitOutcomeInterceptor.Factory<E>,
    private val parsedType: ParsedType<M>,
    private val call: Call<Any>
) : BaseCompositeInterceptor(), RetrofitInterceptor
        where  E : Throwable,
               E : NetworkErrorPredicate {

    private fun List<RetrofitInterceptor.Factory<E>>.create() =
        asSequence().mapNotNull { it.create(parsedType, call) }

    override fun interceptors() =
        interceptors.before.create()
            .plus(errorInterceptor)
            .plus(outcomeInterceptorFactory.create(parsedType, call))
            .plus(interceptors.after.create())

    class Factory<E> internal constructor(
        private val interceptors: RetrofitInterceptors<E>,
        private val errorInterceptor: ErrorInterceptor<E>,
        private val outcomeInterceptorFactory: RetrofitOutcomeInterceptor.Factory<E>
    ) : RetrofitInterceptor.Factory<E>
            where  E : Throwable,
                   E : NetworkErrorPredicate {

        override fun <M> create(
            parsedType: ParsedType<M>,
            call: Call<Any>
        ): RetrofitInterceptor? =
            RetrofitCompositeInterceptor(
                interceptors,
                errorInterceptor,
                outcomeInterceptorFactory,
                parsedType,
                call
            )
    }

}