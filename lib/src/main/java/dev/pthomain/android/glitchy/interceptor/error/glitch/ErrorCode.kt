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

package dev.pthomain.android.glitchy.interceptor.error.glitch

/**
 * Represents a converted type of Error.
 *
 * @param retriable whether or not this exception is transient and whether the call can be silently
 * retried using an exponential backoff for instance.
 */
enum class ErrorCode(val retriable: Boolean) {
    CONFIG(false),
    NETWORK(true),
    UNAUTHORISED(false),
    NOT_FOUND(false),
    UNEXPECTED_RESPONSE(true),
    SERVER_ERROR(true),
    UNKNOWN(true)
}

