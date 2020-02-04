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

package dev.pthomain.android.glitchy.interceptor.error

import dev.pthomain.android.glitchy.interceptor.RetrofitInterceptor
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Interceptor handling network exceptions, converting them using the chosen ErrorFactory and
 * returning a ResponseWrapper holding the response or exception.
 *
 * @see ErrorFactory
 * @param errorFactory the factory converting throwables to custom exceptions
 */
internal class ErrorInterceptor<E> internal constructor(
    private val errorFactory: ErrorFactory<E>
) : RetrofitInterceptor.SimpleInterceptor()
        where E : Exception,
              E : NetworkErrorPredicate {

    /**
     * The composition method converting an upstream response Observable to an Observable emitting
     * a ResponseWrapper holding the response or the converted exception.
     *
     * @param upstream the upstream response Observable, typically as emitted by a Retrofit client.
     * @return the composed Observable emitting a ResponseWrapper and optionally delayed for network availability
     */
    override fun apply(upstream: Observable<Any>) =
        upstream.onErrorResumeNext(Function { Observable.error(errorFactory(it)) })!!

}
