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
import com.kynetics.updatefactory.ddiclient.corek.model.Error
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState

/**
 * @author Daniele Sergio
 */
class SendUpdateResuntMiddleware(private val client: Client) : AbstractUFMiddleware(
        Pair(UFState.Name.APPLYING_SOFTWARE_MODULE, UFEvent.Name.UPDATE_ERROR),
        Pair(UFState.Name.APPLYING_SOFTWARE_MODULE, UFEvent.Name.UPDATE_SUCCESS))  {

    override fun execute(state: UFState, action: UFEvent<*>): UFEvent<*> {
        val actionId = state.data.actionId
        client.postBasedeploymentActionFeedback(actionId,
                DdiActionFeedback(actionId, CurrentTimeFormatter().formatCurrentTime(),getUpdateFeedback(action)))
        return action
    }

    private fun getUpdateFeedback(action:UFEvent<*>): DdiStatus {
        return if(action.name == UFEvent.Name.UPDATE_SUCCESS){
            DdiStatus(DdiStatus.ExecutionStatus.CLOSED, DdiResult(DdiResult.FinalResult.SUCESS, null), emptyList())
        } else {
            action as UFEvent<Error>
            DdiStatus(DdiStatus.ExecutionStatus.CLOSED, DdiResult(DdiResult.FinalResult.FAILURE, null), listOf(*action.payload.details))
        }

    }
}