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
import dev.pthomain.android.boilerplate.core.utils.kotlin.ifElse
import dev.pthomain.android.boilerplate.core.utils.rx.On
import dev.pthomain.android.boilerplate.core.utils.rx.schedule
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome.Error
import dev.pthomain.android.glitchy.core.interceptor.interceptors.outcome.Outcome.Success
import dev.pthomain.android.glitchy.demo.api.CatFactResponse
import dev.pthomain.android.glitchy.demo.api.clients.CatFactFlowClient
import dev.pthomain.android.glitchy.demo.api.clients.CatFactRxJavaClient
import dev.pthomain.android.glitchy.demo.factories.apiErrorRetrofitFlow
import dev.pthomain.android.glitchy.demo.factories.apiErrorRetrofitRxJava
import dev.pthomain.android.glitchy.demo.factories.glitchRetrofitFlow
import dev.pthomain.android.glitchy.demo.factories.glitchRetrofitRxJava
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private var disposable: Disposable? = null
    private var useGlitch = true
    private var useFlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.fact)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val loadButton = findViewById<Button>(R.id.load)
        val handledExceptionButton = findViewById<Button>(R.id.throw_handled_exception)
        val unhandledExceptionButton = findViewById<Button>(R.id.throw_unhandled_exception)

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

        if (useFlow) {
            val flowClient = ifElse(
                useGlitch,
                glitchRetrofitFlow,
                apiErrorRetrofitFlow
            ).create(CatFactFlowClient::class.java)

        } else {
            val rxJavaClient = ifElse(
                useGlitch,
                glitchRetrofitRxJava,
                apiErrorRetrofitRxJava
            ).create(CatFactRxJavaClient::class.java)

            disposable = rxJavaClient.getFact()
                .schedule(On.Io, On.MainThread)
                .doOnError {
                    logger.e(this, it)
                    resetState()
                }
                .subscribe(::onOutcome)
        }
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

}

val throwHandledException = AtomicBoolean(false)
val throwUnhandledException = AtomicBoolean(false)
