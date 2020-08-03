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

package dev.pthomain.android.glitchy.retrofit.type

import dev.pthomain.android.boilerplate.core.utils.kotlin.ifElse
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.getFirstParameterUpperBound
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.rawType
import dev.pthomain.android.glitchy.retrofit.type.OutcomeReturnTypeParser.Companion.OutcomeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class OutcomeReturnTypeParser(
    private val typeTokenResolver: (ParsedType<*>) -> OutcomeToken,
    private val returnSuperTypeParser: ReturnTypeParser<*>
) : ReturnTypeParser<OutcomeToken> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<OutcomeToken> {
        val parsedRxType = returnSuperTypeParser.parseReturnType(returnType, annotations)
        val typeToken = typeTokenResolver(parsedRxType)
        val parsedType = parsedRxType.parsedType

        val (parsedResultType, outcomeType) = if (rawType(parsedType) == Outcome::class.java) {
            val outcomeType = getFirstParameterUpperBound(parsedType)
            wrapReturnType(outcomeType, parsedRxType.rawType) to parsedType
        } else parsedRxType.returnType to parsedType

        return ParsedType(
            typeToken,
            parsedRxType.rawType,
            parsedResultType,
            outcomeType
        )
    }

    private fun wrapReturnType(wrappedType: Type, wrappingType: Type) =
        object : ParameterizedType {
            override fun getRawType() = wrappingType
            override fun getOwnerType() = null
            override fun getActualTypeArguments() = arrayOf(wrappedType)
            override fun toString() = "$wrappingType"
        }

    companion object {
        @JvmStatic
        fun getDefaultInstance(returnSuperTypeParser: ReturnTypeParser<*>) =
            OutcomeReturnTypeParser(
                {
                    ifElse(
                        rawType(it.parsedType) == Outcome::class.java,
                        OutcomeToken.Positive,
                        OutcomeToken.Negative
                    ) as OutcomeToken
                },
                returnSuperTypeParser
            )

        interface IsOutcome
        sealed class OutcomeToken {
            internal object Positive : OutcomeToken(), IsOutcome
            internal object Negative
        }
    }
}