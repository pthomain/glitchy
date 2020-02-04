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

import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.interceptor.CompositeInterceptor
import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

/**
 * Retrofit call adapter composing with DejaVuInterceptor. It takes a type {@code E} for the exception
 * used in generic error handling.
 *
 * @see dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
 */
internal class RetrofitCallAdapter<E, M> private constructor(
    private val compositeInterceptorFactory: CompositeInterceptor.Factory<E>,
    private val parsedType: ParsedType<M>,
    private val rxCallAdapter: CallAdapter<Any, Any>
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
        with(rxCallAdapter.adapt(call)) {
            val interceptor = compositeInterceptorFactory.create(parsedType)

            when (this) {
                is Single<*> -> compose(interceptor)
                is Observable<*> -> compose(interceptor)
                else -> this
            }
        }!!

    /**
     * @return the value type as defined by the default RxJava adapter.
     */
    override fun responseType(): Type = rxCallAdapter.responseType()

    /**
     * Implements the call adapter factory for Retrofit composing the calls with DejaVuInterceptor.
     *
     * @param rxJava2CallAdapterFactory the default RxJava call adapter factory
     * @param logger the logger
     */
    class Factory<E, M> internal constructor(
        private val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        private val compositeInterceptorFactory: CompositeInterceptor.Factory<E>,
        private val returnTypeParser: ReturnTypeParser<M>,
        private val logger: Logger
    ) : CallAdapter.Factory()
            where E : Throwable,
                  E : NetworkErrorPredicate {

        /**
         * Returns a call adapter for interface methods that return {@code returnType}, or null if it
         * cannot be handled by this factory.
         *
         * @param returnType the call's return type
         * @param annotations the call's annotations
         * @param retrofit Retrofit instance
         *
         * @return the Retrofit call adapter handling the cache
         */
        @Suppress("UNCHECKED_CAST")
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *> = RetrofitCallAdapter(
            compositeInterceptorFactory,
            returnTypeParser.parseReturnType(returnType),
            rxJava2CallAdapterFactory.get(
                returnType,
                annotations,
                retrofit
            ) as CallAdapter<Any, Any>
        )
    }
}
