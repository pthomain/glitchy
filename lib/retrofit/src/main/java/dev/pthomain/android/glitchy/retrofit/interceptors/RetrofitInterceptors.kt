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

package dev.pthomain.android.glitchy.retrofit.interceptors

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate

sealed class RetrofitInterceptors<E>(
    override val before: List<RetrofitInterceptor.Factory<E>>,
    override val after: List<RetrofitInterceptor.Factory<E>>
) : Interceptors<RetrofitMetadata<*>, RetrofitInterceptor.Factory<E>> where E : Throwable,
                       E : NetworkErrorPredicate {

    class None<E> : RetrofitInterceptors<E>(emptyList(), emptyList())
            where E : Throwable,
                  E : NetworkErrorPredicate

    class Before<E>(vararg inOrder: RetrofitInterceptor.Factory<E>) :
        RetrofitInterceptors<E>(inOrder.asList(), emptyList())
            where E : Throwable,
                  E : NetworkErrorPredicate

    class After<E>(vararg inOrder: RetrofitInterceptor.Factory<E>) :
        RetrofitInterceptors<E>(emptyList(), inOrder.asList())
            where E : Throwable,
                  E : NetworkErrorPredicate

    class Around<E>(
        before: List<RetrofitInterceptor.Factory<E>>,
        after: List<RetrofitInterceptor.Factory<E>>
    ) : RetrofitInterceptors<E>(before, after)
            where E : Throwable,
                  E : NetworkErrorPredicate

}