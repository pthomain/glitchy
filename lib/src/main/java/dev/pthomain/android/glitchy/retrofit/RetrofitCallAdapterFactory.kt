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
import dev.pthomain.android.glitchy.retrofit.type.ResultReturnTypeParser
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Implements the call adapter factory for Retrofit composing the calls with DejaVuInterceptor.
 *
 * @param rxJava2CallAdapterFactory the default RxJava call adapter factory
 * @param logger the logger
 */
class RetrofitCallAdapterFactory<E, M> internal constructor(
    private val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
    private val compositeInterceptorFactory: CompositeInterceptor.Factory<E>,
    private val returnTypeParser: ReturnTypeParser<M>?,
    private val logger: Logger
) : CallAdapter.Factory()
        where E : Throwable,
              E : NetworkErrorPredicate {

    internal companion object {

        internal fun rawType(type: Type) = getRawType(type)

        internal fun getFirstParameterUpperBound(returnType: Type) =
            if (returnType is ParameterizedType)
                getParameterUpperBound(0, returnType)
            else null
    }

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
    ): CallAdapter<*, *> {
        val parser = returnTypeParser ?: ResultReturnTypeParser.INSTANCE
        val parsedReturnType = parser.parseReturnType(returnType)

        return RetrofitCallAdapter(
            compositeInterceptorFactory,
            parsedReturnType,
            rxJava2CallAdapterFactory.get(
                parsedReturnType.returnType,
                annotations,
                retrofit
            ) as CallAdapter<Any, Any>
        )
    }
}