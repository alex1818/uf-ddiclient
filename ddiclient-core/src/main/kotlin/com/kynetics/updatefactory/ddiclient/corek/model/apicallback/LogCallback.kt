/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.apicallback

import com.google.gson.Gson
import com.kynetics.updatefactory.ddiclient.api.DdiCallback
import com.kynetics.updatefactory.ddiclient.api.model.response.Error
import org.slf4j.LoggerFactory
import retrofit2.Call

/**
 * @author Daniele Sergio
 */
open class LogCallback<T> : DdiCallback<T>() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LogCallback::class.java)
    }

    override fun onError(error: Error) {
        if(LOGGER.isDebugEnabled){
            LOGGER.debug("onError:   " + Gson().toJson(error))
        }
    }

    override fun onSuccess(response: T) {}

    override fun onFailure(call: Call<T>, t: Throwable) {
        if(LOGGER.isDebugEnabled) {
            LOGGER.debug(call.request().url().toString(), t)
        }
    }
}