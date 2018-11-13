/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.apicallback

import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.Error
import retrofit2.Call

/**
 * @author Daniele Sergio
 */
abstract class EventPublisherCallback<T>(protected val eventPublisher: EventPublisher): LogCallback<T>(){
    override fun onError(error:  com.kynetics.updatefactory.ddiclient.api.model.response.Error) {
        super.onError(error)
        eventPublisher.publishEvent(UFEvent.newCommunicationErrorEvent(com.kynetics.updatefactory.ddiclient.corek.model.Error(error.code, arrayOf(error.message), null)))
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        super.onFailure(call, t)
        eventPublisher.publishEvent(UFEvent.newCommunicationErrorEvent(com.kynetics.updatefactory.ddiclient.corek.model.Error(-1, emptyArray(), t)))
    }
}