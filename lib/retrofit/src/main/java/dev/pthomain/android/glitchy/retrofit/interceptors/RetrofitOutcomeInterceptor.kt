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

import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.outcome.OutcomeInterceptor
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser.Companion.IsOutcome
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import io.reactivex.Observable
import retrofit2.Call

class RetrofitOutcomeInterceptor<E, M> private constructor(
    private val delegate: OutcomeInterceptor<E>,
    private val parsedType: ParsedType<M>
) : RetrofitInterceptor.SimpleInterceptor()
        where E : Throwable,
              E : NetworkErrorPredicate {

    override fun apply(upstream: Observable<Any>) =
        if (parsedType.metadata is IsOutcome) delegate.apply(upstream)
        else upstream

    class Factory<E>(
        private val delegate: OutcomeInterceptor<E>
    ) : RetrofitInterceptor.Factory<E>
            where E : Throwable,
                  E : NetworkErrorPredicate {

        override fun <M> create(
            parsedType: ParsedType<M>,
            call: Call<Any>
        ) =
            RetrofitOutcomeInterceptor(
                delegate,
                parsedType
            )
    }


}