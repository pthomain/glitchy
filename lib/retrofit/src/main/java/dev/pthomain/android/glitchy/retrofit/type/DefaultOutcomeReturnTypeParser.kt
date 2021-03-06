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

import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.getFirstParameterUpperBound
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.rawType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface OutcomeReturnTypeParser<M : Any> : ReturnTypeParser<M> {
    val outcomePredicate: (M) -> Boolean
    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<M>
}

class DefaultOutcomeReturnTypeParser<M : Any>(
    private val typeTokenResolver: (ParsedType<*>) -> M,
    private val returnSuperTypeParser: ReturnTypeParser<*>,
    override val outcomePredicate: (M) -> Boolean
) : OutcomeReturnTypeParser<M> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<M> {
        val parsedRxType = returnSuperTypeParser.parseReturnType(returnType, annotations)
        val typeToken = typeTokenResolver(parsedRxType)

        val (reWrappedType, wrappedType) = if (outcomePredicate(typeToken)) {
            val wrappedType = getFirstParameterUpperBound(parsedRxType.wrappedType!!)
            wrapReturnType(wrappedType, parsedRxType.rawType) to wrappedType
        } else parsedRxType.originalType to null

        return ParsedType(
            typeToken,
            returnType,
            parsedRxType.rawType,
            wrappedType,
            reWrappedType
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
        fun getDefaultInstance(
            returnSuperTypeParser: ReturnTypeParser<*>,
            outcomePredicate: (Class<*>) -> Boolean = defaultOutcomePredicate
        ): OutcomeReturnTypeParser<Class<*>> = DefaultOutcomeReturnTypeParser(
            { it.wrappedType?.let(::rawType) ?: Unit::class.java },
            returnSuperTypeParser,
            outcomePredicate
        )

        val defaultOutcomePredicate: (Class<*>) -> Boolean = { it == Outcome::class.java }
    }
}