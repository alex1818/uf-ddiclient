/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.updatefactory.ddiclient.api.model.request.DdiConfigData
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiResult
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiStatus
import com.kynetics.updatefactory.ddiclient.core.UFService
import com.kynetics.updatefactory.ddiclient.core.formatter.CurrentTimeFormatter
import com.kynetics.updatefactory.ddiclient.corek.Client
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.LogCallback
import java.util.*

/**
 * @author Daniele Sergio
 */

class UpdateTargetDataMiddleware(val client: Client, val targetData: UFService.TargetData): AbstractUFMiddleware(Pair(UFState.Name.WAITING,UFEvent.Name.ACTION_FOUND)){

    override fun execute(state: UFState, action: UFEvent<*>): UFEvent<*>{
        action as UFEvent<Map<UFEvent.ActionType,Long>>
        val actionId = action.payload[UFEvent.ActionType.UPDATE_DEVICE_METADATA]
        if(actionId!=null){
            val currentTimeFormatter = CurrentTimeFormatter()
            val configData = DdiConfigData(
                    null,
                    currentTimeFormatter.formatCurrentTime(),
                    DdiStatus(
                            DdiStatus.ExecutionStatus.CLOSED,
                            DdiResult(
                                    DdiResult.FinalResult.SUCESS, null),
                            ArrayList()),
                    targetData.get())
            client.putConfigData(configData).enqueue(LogCallback())
        }
        return action
    }



}