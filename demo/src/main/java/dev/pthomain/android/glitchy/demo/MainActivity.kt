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

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import dev.pthomain.android.boilerplate.core.utils.kotlin.ifElse
import dev.pthomain.android.boilerplate.core.utils.rx.On
import dev.pthomain.android.boilerplate.core.utils.rx.schedule
import dev.pthomain.android.glitchy.core.Glitchy
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.NetworkErrorPredicate
import dev.pthomain.android.glitchy.core.interceptor.interceptors.error.glitch.Glitch
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome.Error
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome.Success
import dev.pthomain.android.glitchy.demo.CatFactClient.Companion.BASE_URL
import dev.pthomain.android.glitchy.retrofit.GlitchyRetrofit
import dev.pthomain.android.glitchy.retrofit.error.RetrofitGlitchFactory
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptor
import dev.pthomain.android.glitchy.retrofit.interceptors.RetrofitInterceptors
import dev.pthomain.android.glitchy.retrofit.type.ParsedType
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private lateinit var glitchRetrofit: Retrofit
    private lateinit var apiErrorRetrofit: Retrofit
    private lateinit var textView: TextView

    private var disposable: Disposable? = null
    private var useGlitch = true

    private val throwHandledException = AtomicBoolean(false)
    private val throwUnhandledException = AtomicBoolean(false)

    private val exceptionInterceptor = object : RetrofitInterceptor.SimpleInterceptor() {
        override fun apply(upstream: Observable<Any>) = upstream.flatMap {
            when {
                throwHandledException.getAndSet(false) -> Observable.error<Any>(
                    IOException("Some IO exception occurred")
                )

                throwUnhandledException.getAndSet(false) -> Observable.error<Any>(
                    NullPointerException("Something was null")
                )

                else -> upstream
            }
        }
    }

    private fun <E> getInterceptors()
            where E : Throwable,
                  E : NetworkErrorPredicate = RetrofitInterceptors.Before(
        object : RetrofitInterceptor.Factory<E> {
            override fun <M> create(
                parsedType: ParsedType<M>,
                call: Call<Any>
            ) = exceptionInterceptor
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.fact)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val loadButton = findViewById<Button>(R.id.load)
        val handledExceptionButton = findViewById<Button>(R.id.throw_handled_exception)
        val unhandledExceptionButton = findViewById<Button>(R.id.throw_unhandled_exception)

        val glitchCallAdapterFactory =
            Glitchy.builder(RetrofitGlitchFactory())
                .extend(GlitchyRetrofit.defaultExtension<Glitch>())
                .withInterceptors(getInterceptors())
                .build()
                .callAdapterFactory

        val apiErrorCallAdapterFactory =
            Glitchy.builder(ApiError.Factory())
                .extend(GlitchyRetrofit.defaultExtension<ApiError>())
                .withInterceptors(getInterceptors())
                .build()
                .callAdapterFactory

        glitchRetrofit = getRetrofit(glitchCallAdapterFactory)
        apiErrorRetrofit = getRetrofit(apiErrorCallAdapterFactory)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            useGlitch = checkedId == R.id.glitch
        }

        handledExceptionButton.setOnClickListener {
            throwHandledException.set(true)
            loadFact()
        }
        unhandledExceptionButton.setOnClickListener {
            throwUnhandledException.set(true)
            loadFact()
        }

        loadButton.setOnClickListener {
            loadFact()
        }
    }

    private fun loadFact() {
        textView.text = getString(R.string.loading)

        val client = ifElse(
            useGlitch,
            glitchRetrofit,
            apiErrorRetrofit
        ).create(CatFactClient::class.java)

        disposable = client.getFact()
            .schedule(On.Io, On.MainThread)
            .doOnError { resetState() }
            .subscribe(::onOutcome)
    }

    private fun onOutcome(outcome: Outcome<CatFactResponse>) {
        textView.text = when (outcome) {

            is Success -> getString(R.string.cat_fact, outcome.response.fact)

            is Error<*> -> with(outcome.exception) {
                getString(
                    R.string.handled_error,
                    "${javaClass.simpleName}: $message"
                )
            }
        }
    }

    private fun resetState() {
        textView.post { textView.text = "" }
        throwHandledException.set(false)
        throwUnhandledException.set(false)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    private fun getRetrofit(callAdapterFactory: CallAdapter.Factory) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(callAdapterFactory)
            .build()

}
