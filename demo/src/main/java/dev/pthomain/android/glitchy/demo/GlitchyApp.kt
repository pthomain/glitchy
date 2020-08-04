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

import android.app.Application
import android.widget.Toast
import dev.pthomain.android.boilerplate.core.utils.log.Logger
import dev.pthomain.android.boilerplate.core.utils.log.SimpleLogger
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins

class GlitchyApp : Application(), Consumer<Throwable> {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler(this)
        logger = SimpleLogger(true, packageName)
    }

    /**
     * This is where you handle any unexpected (i.e. unhandled by the ErrorFactory)
     * application-wide exceptions. These should represent any exception that would
     * result from coding errors and that SHOULD NOT be silently ignored.
     * For instance, you can remotely track those exceptions.
     *
     * @see dev.pthomain.android.glitchy.interceptor.error.ErrorFactory
     */
    override fun accept(t: Throwable) {
        val message = getString(
            R.string.unhandled_error,
            "${t.javaClass.simpleName}: ${t.message}"
        )
        logger.e(this, t, message)
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

lateinit var logger: Logger
