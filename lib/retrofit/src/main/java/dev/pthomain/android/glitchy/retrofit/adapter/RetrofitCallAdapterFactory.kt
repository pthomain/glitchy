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

package dev.pthomain.android.glitchy.retrofit.adapter

import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptorFactory
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Implements the call adapter factory for Retrofit composing the calls with CompositeInterceptor.
 *
 * @param defaultCallAdapterFactory the default Retrofit call adapter factory
 * @param logger the logger
 */
class RetrofitCallAdapterFactory<M> internal constructor(
    private val defaultCallAdapterFactory: CallAdapter.Factory,
    private val compositeInterceptorFactory: RetrofitInterceptorFactory<M>,
    private val returnTypeParser: ReturnTypeParser<M>,
    private val logger: Logger
) : CallAdapter.Factory() {

    companion object {

        @JvmStatic
        fun rawType(type: Type): Class<*> = getRawType(type)

        @JvmStatic
        fun getFirstParameterUpperBound(returnType: Type) =
            (returnType as? ParameterizedType)
                ?.let { getParameterUpperBound(0, it) }
                ?: throw IllegalStateException(
                    returnType.javaClass.simpleName.let {
                        "$it is missing a generic parameter: $it<T>"
                    }
                )
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
        val parsedReturnType = returnTypeParser.parseReturnType(returnType, annotations)

        val defaultCallAdapter = defaultCallAdapterFactory.get(
            parsedReturnType.returnType,
            annotations,
            retrofit
        ) as CallAdapter<Any, Any>

        return RetrofitCallAdapter(
            compositeInterceptorFactory,
            parsedReturnType,
            defaultCallAdapter
        )
    }
}