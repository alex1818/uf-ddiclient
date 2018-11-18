/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.timertask

import com.kynetics.updatefactory.ddiclient.api.model.response.DdiControllerBase
import com.kynetics.updatefactory.ddiclient.corek.Client
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.EventPublisherCallback
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Daniele Sergio
 */
class PollingTask(private val client: Client, private val eventPublisher: EventPublisher) : TimerTask() {
    override fun run() {
        client.getControllerBase().enqueue(object : EventPublisherCallback<DdiControllerBase>(eventPublisher){
            override fun onSuccess(response: DdiControllerBase) {
                super.onSuccess(response)
                var sleepTime = 30_000L
                val simpleDateFormat = SimpleDateFormat("hh:mm:ss")
                try {
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val date = simpleDateFormat.parse(response.config.polling.sleep)
                    sleepTime = date.time
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val actions = getActions(response)
                if(actions.isEmpty()){
                    eventPublisher.publishEvent(UFEvent.newNoActionFoundEvent(sleepTime))
                } else {
                    eventPublisher.publishEvent(UFEvent.newActionsFoundEvent(actions))
                }

            }
        })
    }

    private fun getActions(response: DdiControllerBase):Map<UFEvent.ActionType, Long>{
        val actions = mutableMapOf<UFEvent.ActionType, Long>()

        val configDataLink = response.getLink("configData")
        if (configDataLink != null) {
            actions[UFEvent.ActionType.UPDATE_DEVICE_METADATA] = 0
        }

        val deploymentBaseLink = response.getLink("deploymentBase")
        if (deploymentBaseLink != null) {
            actions[UFEvent.ActionType.NEW_UPDATE] = deploymentBaseLink.parseLink().actionId
        }

        val cancelAction = response.getLink("cancelAction")
        if (cancelAction != null) {
            actions[UFEvent.ActionType.CANCEL_UPDATE] = cancelAction.parseLink().actionId
        }

        return actions
    }
}