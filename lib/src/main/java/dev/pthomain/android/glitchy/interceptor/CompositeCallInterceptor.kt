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

package dev.pthomain.android.glitchy.interceptor

import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate
import io.reactivex.Observable
import io.reactivex.ObservableSource
import retrofit2.Call
import java.util.*

class CompositeCallInterceptor<E> private constructor(
    private val callInterceptors: LinkedList<Interceptor.CallFactory<E>>,
    private val call: Call<Any>
) : Interceptor.SimpleInterceptor()
        where  E : Throwable,
               E : NetworkErrorPredicate {

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {
        var intercepted = upstream

        callInterceptors
            .asSequence()
            .mapNotNull { it.create(call) }
            .forEach { intercepted = intercepted.compose(it) }

        return intercepted
    }

    class Factory<E> internal constructor(
        private val callInterceptors: LinkedList<Interceptor.CallFactory<E>>
    ) : Interceptor.CallFactory<E>
            where  E : Throwable,
                   E : NetworkErrorPredicate {

        override fun create(call: Call<Any>) =
            CompositeCallInterceptor(
                callInterceptors,
                call
            )
    }

}