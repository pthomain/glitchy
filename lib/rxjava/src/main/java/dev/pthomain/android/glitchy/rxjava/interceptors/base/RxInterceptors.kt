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

package dev.pthomain.android.glitchy.rxjava.interceptors.base

import dev.pthomain.android.glitchy.core.interceptor.interceptors.base.Interceptors

sealed class RxInterceptors(
    override val before: List<RxInterceptor>,
    override val after: List<RxInterceptor>
) : Interceptors<RxInterceptor> {

    class None : RxInterceptors(emptyList(), emptyList())

    class Before(vararg inOrder: RxInterceptor) : RxInterceptors(inOrder.asList(), emptyList())

    class After(vararg inOrder: RxInterceptor) : RxInterceptors(emptyList(), inOrder.asList())

    class Around(
        before: List<RxInterceptor>,
        after: List<RxInterceptor>
    ) : RxInterceptors(before, after)

}