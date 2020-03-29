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
import dev.pthomain.android.glitchy.interceptor.outcome.Outcome
import dev.pthomain.android.glitchy.retrofit.RetrofitCallAdapterFactory.Companion.getFirstParameterUpperBound
import dev.pthomain.android.glitchy.retrofit.RetrofitCallAdapterFactory.Companion.rawType
import io.reactivex.Single
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class OutcomeReturnTypeParser<M>(
    private val metadataResolver: (ParsedType<*>) -> M
) : ReturnTypeParser<M> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<M> {
        val parsedRxType = RxReturnTypeParser.INSTANCE.parseReturnType(returnType, annotations)
        val parsedType = parsedRxType.parsedType

        val (parsedResultType, outcomeType) = if (rawType(parsedType) == Outcome::class.java ) {
            val outcomeType = getFirstParameterUpperBound(parsedType)!!
            wrapToSingle(outcomeType) to parsedType
        } else parsedRxType.returnType to parsedType

        return ParsedType(
            metadataResolver(parsedRxType),
            parsedResultType,
            outcomeType
        )
    }

    private fun wrapToSingle(outcomeType: Type): Type = object : ParameterizedType {
        override fun getRawType() = Single::class.java
        override fun getOwnerType() = null
        override fun getActualTypeArguments() = arrayOf(outcomeType)
    }

    companion object {
        @JvmStatic
        val INSTANCE = OutcomeReturnTypeParser {
            ifElse(
                rawType(it.parsedType) == Outcome::class.java,
                OutcomeToken,
                Unit
            )
        }

        interface IsOutcome
        object OutcomeToken : IsOutcome
    }

}