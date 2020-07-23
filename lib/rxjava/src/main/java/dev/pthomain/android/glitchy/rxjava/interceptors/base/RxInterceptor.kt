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
import io.reactivex.Single

sealed class RxInterceptor<I, O> : Interceptor {

    abstract class ObservableInterceptor
        : RxInterceptor<Observable<Any>, Observable<Any>>(),
        ObservableComposer {

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> intercept(upstream: T) = when (upstream) {
            is Observable<*> -> apply(upstream as Observable<Any>)
            else -> throw IllegalArgumentException("Expected an Observable, was: $upstream")
        } as T

    }

    abstract class SingleInterceptor
        : RxInterceptor<Single<Any>, Single<Any>>(),
        SingleComposer {

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> intercept(upstream: T) = when (upstream) {
            is Single<*> -> apply(upstream as Single<Any>)
            else -> throw IllegalArgumentException("Expected a Single, was: $upstream")
        } as T

    }

    abstract class CombinedRxInterceptor
        : RxInterceptor.ObservableInterceptor(),
        SingleComposer {

        override fun apply(upstream: Single<Any>) = upstream
            .toObservable()
            .compose { apply(it) }
            .firstOrError()!!

    }
}

interface ObservableComposer {
    fun apply(upstream: Observable<Any>): Observable<Any>
}

interface SingleComposer {
    fun apply(upstream: Single<Any>): Single<Any>
}