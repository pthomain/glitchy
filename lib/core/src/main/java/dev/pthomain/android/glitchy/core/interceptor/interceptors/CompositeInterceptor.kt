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

package dev.pthomain.android.glitchy.core.interceptor.interceptors

import dev.pthomain.android.glitchy.core.interceptor.error.ErrorInterceptor
import dev.pthomain.android.glitchy.core.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.outcome.OutcomeInterceptor
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.SingleSource

class CompositeInterceptor<E>(
    private val interceptors: Interceptors,
    private val errorInterceptorFactory: ErrorInterceptor<E>,
    private val outcomeInterceptorFactory: OutcomeInterceptor<E>
) : Interceptor
        where  E : Throwable,
               E : NetworkErrorPredicate {

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {
        var intercepted = upstream
        interceptors().forEach { intercepted = intercepted.compose(it) }
        return intercepted
    }

    override fun apply(upstream: Single<Any>): SingleSource<Any> {
        var intercepted = upstream
        interceptors().forEach { intercepted = intercepted.compose(it) }
        return intercepted
    }

    private fun interceptors() =
        interceptors.before.asSequence()
            .plus(errorInterceptorFactory)
            .plus(outcomeInterceptorFactory)
            .plus(interceptors.after.asSequence())

}