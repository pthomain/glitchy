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

sealed class RxInterceptor : Interceptor {

    abstract class ObservableInterceptor : RxInterceptor(), ObservableComposer {

        @Suppress("UNCHECKED_CAST")
        final override fun intercept(upstream: Any) = when (upstream) {
            is Observable<*> -> apply(upstream as Observable<Any>)
            else -> throw IllegalArgumentException("Expected an Observable, was: $upstream")
        }

    }

    abstract class SingleInterceptor : RxInterceptor(), SingleComposer {

        @Suppress("UNCHECKED_CAST")
        final override fun intercept(upstream: Any) = when (upstream) {
            is Single<*> -> apply(upstream as Single<Any>)
            else -> throw IllegalArgumentException("Expected a Single, was: $upstream")
        }

    }

    abstract class CombinedRxInterceptor : RxInterceptor(), ObservableComposer, SingleComposer {

        @Suppress("UNCHECKED_CAST")
        final override fun intercept(upstream: Any): Any = when (upstream) {
            is Observable<*> -> apply(upstream as Observable<Any>)
            is Single<*> -> apply(upstream as Single<Any>)
            else -> throw IllegalArgumentException("Expected an Observable or Single, was: $upstream")
        }

        final override fun apply(upstream: Single<Any>) = upstream
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