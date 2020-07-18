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

package dev.pthomain.android.glitchy.rxjava.interceptors

import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import dev.pthomain.android.glitchy.rxjava.interceptors.base.RxInterceptor
import io.reactivex.Observable
import io.reactivex.functions.Function

 class OutcomeRxInterceptor<E> internal constructor(
     private val errorFactory: ErrorFactory<E>
 ) : RxInterceptor.SimpleRxInterceptor()
         where E : Throwable,
               E : NetworkErrorPredicate {

     override fun apply(upstream: Observable<Any>): Observable<Any> =
         upstream
             .map { Outcome.Success(it) as Any }
             .onErrorResumeNext(Function {
                 errorFactory.asHandledError(it)
                    ?.let { Observable.just<Any>(it) }
                    ?: Observable.error<Any>(it)
            })!!

}