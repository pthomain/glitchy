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

package dev.pthomain.android.glitchy.core.interceptor.interceptors.base

import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate

internal class CompositeInterceptor<E, M, out F : InterceptorFactory<M>> private constructor(
    private val interceptors: Interceptors<M, F>,
    private val errorInterceptor: Interceptor,
    private val outcomeInterceptor: Interceptor?,
    private val metadata: M?
) : BaseCompositeInterceptor()
        where  E : Throwable,
               E : NetworkErrorPredicate {

    override fun interceptors() =
        interceptors.before.asSequence().map { it.create(metadata) }
            .plus(errorInterceptor)
            .plus(outcomeInterceptor)
            .plus(interceptors.after.asSequence().map { it.create(metadata) })
            .filterNotNull()

    class Factory<E, M, F : InterceptorFactory<M>>(
        private val interceptors: Interceptors<M, F>,
        private val errorInterceptor: Interceptor,
        private val outcomeInterceptor: Interceptor?,
    ) : InterceptorFactory<M>
            where  E : Throwable,
                   E : NetworkErrorPredicate {

        override fun create(metadata: M?) = CompositeInterceptor<E, M, F>(
            interceptors,
            errorInterceptor,
            outcomeInterceptor,
            metadata
        )
    }
}