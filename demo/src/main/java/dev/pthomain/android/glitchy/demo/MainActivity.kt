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

package dev.pthomain.android.glitchy.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import dev.pthomain.android.boilerplate.core.utils.rx.On
import dev.pthomain.android.boilerplate.core.utils.rx.schedule
import dev.pthomain.android.glitchy.Glitchy
import dev.pthomain.android.glitchy.interceptor.outcome.Outcome
import dev.pthomain.android.glitchy.interceptor.outcome.Outcome.*
import io.reactivex.Single
import io.reactivex.functions.Consumer
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val glitchCallAdapterFactory = Glitchy.createCallAdapterFactory()
        val apiErrorCallAdapterFactory = Glitchy.createCallAdapterFactory<ApiError, Any>(
            ApiError.Factory()
        )

        val glitchRetrofit = getRetrofit(glitchCallAdapterFactory)
        val apiErrorRetrofit = getRetrofit(apiErrorCallAdapterFactory)

        val testClient = glitchRetrofit.create(TestClient::class.java)

        testClient.getFact()
            .schedule(On.Io, On.MainThread)
            .subscribe(Consumer {
                val message = when (it) {
                    is Success -> it.response.fact
                    is Error<*> -> it.exception.message
                }

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    interface TestClient {

        @GET(ENDPOINT)
        fun getFact(): Single<Outcome<CatFactResponse>>

    }

    private fun getRetrofit(callAdapterFactory: CallAdapter.Factory) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(callAdapterFactory)
            .build()

    companion object {
        internal const val BASE_URL = "https://catfact.ninja/"
        internal const val ENDPOINT = "fact"
    }
}
