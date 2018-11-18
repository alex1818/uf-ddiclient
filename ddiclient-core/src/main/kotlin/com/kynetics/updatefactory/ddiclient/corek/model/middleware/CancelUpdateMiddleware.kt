/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.updatefactory.ddiclient.api.model.request.DdiActionFeedback
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiResult
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiStatus
import com.kynetics.updatefactory.ddiclient.core.formatter.CurrentTimeFormatter
import com.kynetics.updatefactory.ddiclient.corek.Client
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.EventPublisherCallback
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.LogCallback

/**
 * @author Daniele Sergio
 */

class CancelUpdateMiddleware(val client: Client, val eventPublisher: EventPublisher): AbstractUFMiddleware(
        Pair(UFState.Name.WAITING,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.COMMUNICATION_ERROR,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.UPDATE_INITIALIZATION,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.SAVING_FILE,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION,UFEvent.Name.ACTION_FOUND),
        Pair(UFState.Name.APPLYING_SOFTWARE_MODULE,UFEvent.Name.ACTION_FOUND)){

    override fun execute(state: UFState, action: UFEvent<*>): UFEvent<*>{
        action as UFEvent<Map<UFEvent.ActionType,Long>>

        val actionId = action.payload[UFEvent.ActionType.CANCEL_UPDATE] ?: return action

        val response =   client.getControllerCancelAction(actionId).execute()//todo use enqueue

        if(actionId in arrayOf(state.data.actionId, state.data.proxyState?.actionId)
                && !state.data.updateStarted){
            val status = DdiStatus(DdiStatus.ExecutionStatus.CLOSED, DdiResult(DdiResult.FinalResult.SUCESS, null), emptyList())
            val feedback = DdiActionFeedback(actionId, CurrentTimeFormatter().formatCurrentTime(),status)
            client.postCancelActionFeedback(response.body()?.id?.toLong(),feedback).enqueue(EventPublisherCallback<Void>(eventPublisher))
        } else {//todo check REJECTED value is correct
            val status = DdiStatus(DdiStatus.ExecutionStatus.REJECTED, DdiResult(DdiResult.FinalResult.SUCESS, null), emptyList())
            val feedback = DdiActionFeedback(actionId, CurrentTimeFormatter().formatCurrentTime(),status)
            client.postCancelActionFeedback(response.body()?.id?.toLong(),feedback).enqueue(LogCallback<Void>())
            interruptDispatching()
        }

        return action //todo update action to go to CANCELLING_UPDATE state
    }

}


