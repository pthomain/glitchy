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

package dev.pthomain.android.glitchy.retrofit.flow.type

import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.getFirstParameterUpperBound
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.rawType
import dev.pthomain.android.glitchy.retrofit.flow.type.FlowReturnTypeParser.FlowToken
import dev.pthomain.android.glitchy.retrofit.flow.type.FlowReturnTypeParser.FlowToken.Negative
import dev.pthomain.android.glitchy.retrofit.flow.type.FlowReturnTypeParser.FlowToken.Positive
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

class FlowReturnTypeParser(
    private val metadataResolver: (Type) -> FlowToken
) : ReturnTypeParser<FlowToken> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<FlowToken> =
        with(rawType(returnType)) {
            ParsedType(
                metadataResolver(returnType),
                this,
                returnType,
                extractParam(returnType, this)
            )
        }

    private fun extractParam(returnType: Type, rawType: Type) =
        with(rawType) {
            when (this) {
                Flow::class.java -> getFirstParameterUpperBound(returnType)
                else -> returnType
            }
        }

    companion object {
        @JvmStatic
        val DEFAULT = FlowReturnTypeParser {
            when (it) {
                Flow::class.java -> Positive
                else -> Negative
            }
        }
    }

    sealed class FlowToken {
        internal object Positive : FlowToken()
        internal object Negative : FlowToken()
    }
}