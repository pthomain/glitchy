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

package dev.pthomain.android.glitchy.core.interceptor.interceptors.error

import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome


/**
 * Converts a given Throwable into a new type extending from Exception and NetworkErrorPredicate
 */
interface ErrorFactory<E> : (Throwable) -> E
        where E : Throwable,
              E : NetworkErrorPredicate {

    val exceptionClass: Class<E>

    /**
     * Returns this Throwable as a Result.Error if its type is handled by the factory
     * and the cause is recognised as a handled error (i.e. an error that should be
     * intercepted and returned as a Result via onNext() rather than be emitted via onError()).
     * The logic above would only apply to types defined as Result in the Retrofit call.
     * Exceptions that cannot be returned as a result will always be
     * delivered via onError().
     *
     * @see dev.pthomain.android.glitchy.interceptor.outcome.Outcome
     */
    fun asHandledError(throwable: Throwable): Outcome.Error<E>?

}
