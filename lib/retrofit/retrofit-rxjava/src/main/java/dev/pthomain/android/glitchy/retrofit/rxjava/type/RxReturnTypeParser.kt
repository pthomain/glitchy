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

package dev.pthomain.android.glitchy.retrofit.rxjava.type

import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.getFirstParameterUpperBound
import dev.pthomain.android.glitchy.retrofit.adapter.RetrofitCallAdapterFactory.Companion.rawType
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.reflect.Type

class RxReturnTypeParser(
    private val typeTokenResolver: (Type) -> Class<*>
) : ReturnTypeParser<Class<*>> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ) =
        with(rawType(returnType)) {
            ParsedType(
                typeTokenResolver(returnType),
                this,
                returnType,
                extractParam(returnType, this)
            )
        }

    private fun extractParam(returnType: Type, rawType: Type) =
        when (rawType) {
            Single::class.java,
            Observable::class.java -> getFirstParameterUpperBound(returnType)
            else -> returnType
        }


    companion object {
        @JvmStatic
        val DEFAULT = RxReturnTypeParser {
            with(rawType(it)) {
                when (this) {
                    Single::class.java,
                    Observable::class.java -> this
                    else -> Unit.javaClass
                }
            }
        }
    }
}