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

import dev.pthomain.android.glitchy.interceptor.CompositeCallInterceptor
import dev.pthomain.android.glitchy.interceptor.CompositeTypeInterceptor
import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Retrofit call adapter composing with DejaVuInterceptor. It takes a type {@code E} for the exception
 * used in generic error handling.
 *
 * @see dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
 */
internal class RetrofitCallAdapter<E, M>(
    private val compositeTypeInterceptorFactory: CompositeTypeInterceptor.Factory<E>,
    private val compositeCallInterceptorFactory: CompositeCallInterceptor.Factory<E>,
    private val parsedType: ParsedType<M>,
    private val glitchyCallAdapter: CallAdapter<Any, Any>
) : CallAdapter<Any, Any>
        where E : Throwable,
              E : NetworkErrorPredicate {

    /**
     * Adapts the call by composing it with a DejaVuInterceptor if a cache operation is provided
     * via any of the supported methods (cache predicate, header or annotation)
     * or via the default RxJava call adapter otherwise.
     *
     * @param call the current Retrofit call
     * @return the call adapted to RxJava type
     */
    override fun adapt(call: Call<Any>) =
        with(glitchyCallAdapter.adapt(call)) {
            val typeInterceptor = compositeTypeInterceptorFactory.create(parsedType)
            val callInterceptor = compositeCallInterceptorFactory.create(parsedType, call)

            when (this) {
                is Single<*> -> compose(typeInterceptor).compose(callInterceptor)
                is Observable<*> -> compose(typeInterceptor).compose(callInterceptor)
                else -> this
            }
        }!!

    /**
     * @return the value type as defined by the default RxJava adapter.
     */
    override fun responseType(): Type = glitchyCallAdapter.responseType()

}
