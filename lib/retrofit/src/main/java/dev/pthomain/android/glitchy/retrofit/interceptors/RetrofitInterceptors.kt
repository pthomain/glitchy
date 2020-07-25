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

sealed class RetrofitInterceptors<M>(
    override val before: List<RetrofitInterceptor.Factory<M>>,
    override val after: List<RetrofitInterceptor.Factory<M>>
) : Interceptors<RetrofitMetadata<M>,
        RetrofitInterceptor.Factory<M>> {

    class None<M> : RetrofitInterceptors<M>(emptyList(), emptyList())

    class Before<M>(vararg inOrder: RetrofitInterceptor.Factory<M>) :
        RetrofitInterceptors<M>(inOrder.asList(), emptyList())

    class After<M>(vararg inOrder: RetrofitInterceptor.Factory<M>) :
        RetrofitInterceptors<M>(emptyList(), inOrder.asList())

    class Around<M>(
        before: List<RetrofitInterceptor.Factory<M>>,
        after: List<RetrofitInterceptor.Factory<M>>
    ) : RetrofitInterceptors<M>(before, after)

}