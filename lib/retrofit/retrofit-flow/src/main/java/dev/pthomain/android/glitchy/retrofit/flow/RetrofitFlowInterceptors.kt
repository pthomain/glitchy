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

package dev.pthomain.android.glitchy.retrofit.flow

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.InterceptorFactory
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptors
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadataInterceptors

object RetrofitFlowInterceptors {

    fun <M> none(): RetrofitMetadataInterceptors<M> =
        FlowInterceptors.None()

    fun <M> before(vararg inOrder: InterceptorFactory<RetrofitMetadata<M>>): RetrofitMetadataInterceptors<M> =
        FlowInterceptors.Before(*inOrder)

    fun <M> after(vararg inOrder: InterceptorFactory<RetrofitMetadata<M>>): RetrofitMetadataInterceptors<M> =
        FlowInterceptors.After(*inOrder)

    fun <M> around(
        before: List<InterceptorFactory<RetrofitMetadata<M>>>,
        after: List<InterceptorFactory<RetrofitMetadata<M>>>
    ): RetrofitMetadataInterceptors<M> =
        FlowInterceptors.Around(before, after)

}