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

package dev.pthomain.android.glitchy.demo

import dev.pthomain.android.boilerplate.core.utils.kotlin.ifElse
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.ErrorFactory
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import java.io.IOException

class ApiError(override val cause: Throwable) : Throwable(), NetworkErrorPredicate {

    override fun isNetworkError() = cause is IOException

    override val message = "This is a custom ${ifElse(
        isNetworkError(),
        "handled",
        "unhandled"
    )} ApiError:\n$cause"

    class Factory : ErrorFactory<ApiError> {

        override val exceptionClass = ApiError::class.java

        override fun invoke(p1: Throwable) = ApiError(p1)

        override fun asHandledError(throwable: Throwable) =
            if (throwable is ApiError && throwable.isNetworkError())
                Outcome.Error(throwable)
            else null
    }
}