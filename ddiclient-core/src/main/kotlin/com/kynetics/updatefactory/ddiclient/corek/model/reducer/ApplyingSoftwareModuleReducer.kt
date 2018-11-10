/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.reducer

import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.Error

/**
 * @author Daniele Sergio
 */
class ApplyingSoftwareModuleReducer : AbstractReducer(UFState.Name.APPLYING_SOFTWARE_MODULE) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(action.name){
            UFEvent.Name.UPDATE_ERROR   -> UFState(UFState.Name.SENDING_UPDATE_STATUS, state.data.copy(updateResponse = UFState.UpdateResponse(false, (action as UFEvent<Error>).payload.details)))
            UFEvent.Name.UPDATE_SUCCESS -> getNextStateOnUpdateSuccess(state)
            else                        -> state
        }
    }

    private fun getNextStateOnUpdateSuccess(state: UFState): UFState {
        val distribution = state.data.distribution
        return if (distribution!!.hasNextSoftwareModule()){
            UFState(UFState.Name.SAVING_FILE, state.data.copy(distribution = distribution!!.nextStep(true)))
        } else {
            UFState(UFState.Name.SENDING_UPDATE_STATUS, state.data.copy(updateResponse = UFState.UpdateResponse(!distribution!!.error, emptyArray())))
        }
    }
}