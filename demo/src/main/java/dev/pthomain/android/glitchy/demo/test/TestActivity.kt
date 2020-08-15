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

package dev.pthomain.android.glitchy.demo.test

import android.app.Activity
import android.os.Bundle
import com.google.gson.Gson
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.glitch.GlitchFactory
import dev.pthomain.android.glitchy.demo.api.BASE_URL
import dev.pthomain.android.glitchy.demo.api.clients.CatFactFlowClient
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptor
import dev.pthomain.android.glitchy.flow.interceptors.base.FlowInterceptors
import dev.pthomain.android.glitchy.retrofit.flow.GlitchyRetrofitFlow
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glitchyFlow = GlitchyRetrofitFlow.Default.builder(
            GlitchFactory(),
            FlowInterceptors.Before(::LoggingInterceptor)
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(glitchyFlow.callAdapterFactory)
            .build()


        val flowClient = retrofit.create(CatFactFlowClient::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            flowClient.getFact()
        }
    }

}

class LoggingInterceptor(private val metadata: RetrofitMetadata<Any>?) : FlowInterceptor() {
    override fun interceptFlow(upstream: Flow<*>): Flow<*> {

        return upstream
//            .transform {
//            Log.d("INTERCEPTED")
//            Unit
//        }
    }

}