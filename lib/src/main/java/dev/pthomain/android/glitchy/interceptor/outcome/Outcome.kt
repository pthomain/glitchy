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

package dev.pthomain.android.glitchy.interceptor.outcome

import dev.pthomain.android.glitchy.interceptor.error.NetworkErrorPredicate

/**
 * Represents either a successful response (Success) or a handled parsed exception
 * returned by an ErrorFactory. Handled exceptions are evaluated by ErrorFactory::isResult.
 *
 * @see dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
 */
sealed class Outcome<R> {

    class Success<R>(val response: R) : Outcome<R>()

    class Error<E>(val exception: E) : Outcome<Nothing>()
            where E : Throwable, E : NetworkErrorPredicate

}