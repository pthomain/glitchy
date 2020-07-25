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

import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser.Companion.IsOutcome

class RetrofitOutcomeInterceptor<M> private constructor(
    private val outcomeInterceptor: RetrofitInterceptor<M>,
    private val metadata: RetrofitMetadata<M>?
) : RetrofitInterceptor<M>() {

    override fun <T : Any> intercept(upstream: T, metadata: RetrofitMetadata<M>?) =
        if (metadata is IsOutcome) outcomeInterceptor.intercept(upstream)
        else upstream

    //TODO remove
    class Factory<M> internal constructor(
        private val outcomeInterceptor: RetrofitInterceptor<M>
    ) : RetrofitInterceptor.Factory<M> {

        override fun create(metadata: RetrofitMetadata<M>?) = RetrofitOutcomeInterceptor(
            outcomeInterceptor,
            metadata
        )

    }

}