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

package dev.pthomain.android.glitchy.rxjava.interceptors.base

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptor
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer

interface RxInterceptor
    : Interceptor,
    ObservableTransformer<Any, Any>,
    SingleTransformer<Any, Any> {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> intercept(upstream: T) = when (upstream) {
        is Observable<*> -> apply(upstream as Observable<Any>) as T
        is Single<*> -> apply(upstream as Single<Any>) as T
        else -> throw IllegalArgumentException("Invalid argument: $upstream")
    }

    abstract class SimpleRxInterceptor : RxInterceptor {

        override fun apply(upstream: Single<Any>) = upstream
            .toObservable()
            .compose(this)
            .firstOrError()!!

    }
}
