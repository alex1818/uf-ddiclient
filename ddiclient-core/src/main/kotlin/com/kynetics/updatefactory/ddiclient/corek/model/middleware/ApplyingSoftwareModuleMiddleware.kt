/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.updatefactory.ddiclient.core.servicecallback.SystemOperation
import com.kynetics.updatefactory.ddiclient.corek.model.Error
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState

/**
 * @author Daniele Sergio
 */
class ApplyingSoftwareModuleMiddleware(private val eventPublisher: EventPublisher,
                                       private val systemOperation: SystemOperation): AbstractUFMiddleware(
        Pair(UFState.Name.WAITING_UPDATE_AUTHORIZATION, UFEvent.Name.AUTHORIZATION_GRANTED),
        Pair(UFState.Name.SAVING_FILE, UFEvent.Name.FILE_SAVED)) {

    override fun execute(state: UFState, action: UFEvent<*>): UFEvent<*> {
        if(state.name == UFState.Name.WAITING_UPDATE_AUTHORIZATION || state.data.isUpdateForced!!){
            this.executeUpdate(state)
        }
        return action
    }

    private fun executeUpdate(state: UFState) {
        Thread {
            //todo replace with coroutine
            when (systemOperation.executeUpdate(state.data.actionId!!).get()!!) {
                SystemOperation.UpdateStatus.SUCCESSFULLY_APPLIED ->
                    eventPublisher.publishEvent(event = UFEvent.newUpdateSuccessEvent)
                SystemOperation.UpdateStatus.APPLIED_WITH_ERROR ->
                    eventPublisher.publishEvent(event = UFEvent.newUpdateErrorEvent(Error(details = arrayOf("Error"))))
            }
        }.start()
    }
}