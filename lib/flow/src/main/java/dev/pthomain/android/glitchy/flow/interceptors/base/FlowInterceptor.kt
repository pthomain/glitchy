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

package dev.pthomain.android.glitchy.flow.interceptors.base

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

abstract class FlowInterceptor : Interceptor {

    @Suppress("UNCHECKED_CAST")
    final override fun intercept(upstream: Any) = when (upstream) {
        is Flow<*> -> flatMap(upstream as Flow<Any>)
        else -> throw IllegalArgumentException("Invalid argument: $upstream")
    }

    open fun flatMap(upstream: Flow<Any>): Flow<Any> = upstream.transform { value ->
        emit(map(value))
    }

    open suspend fun map(value: Any): Any = value

}