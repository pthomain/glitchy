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
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import dev.pthomain.android.glitchy.retrofit.type.ReturnTypeParser
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

class FlowReturnTypeParser<M : Any>(
    private val metadataResolver: (Type) -> M
) : ReturnTypeParser<M> {

    override fun parseReturnType(
        returnType: Type,
        annotations: Array<Annotation>
    ): ParsedType<M> = ParsedType(
        metadataResolver(returnType),
        returnType,
        extractParam(returnType)
    )

    private fun extractParam(returnType: Type) =
        with(rawType(returnType)) {
            when (this) {
                Flow::class.java -> getFirstParameterUpperBound(returnType)!!
                else -> this
            }
        }

    companion object {
        @JvmStatic
        fun getDefaultInstance() = FlowReturnTypeParser { Unit }
    }
}