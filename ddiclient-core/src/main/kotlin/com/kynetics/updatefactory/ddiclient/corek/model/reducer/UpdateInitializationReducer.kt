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

/**
 * @author Daniele Sergio
 */
class UpdateInitializationReducer() : AbstractReducer(UFState.Name.UPDATE_INITIALIZATION) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return if(action.name == UFEvent.Name.UPDATE_INITIALIZED){
            action as UFEvent<UFEvent.UpdateMetadata>
            val updateMetadata = action.payload
            val updatedData =  state.data.copy(isForced = updateMetadata.isForced, distribution = updateMetadata.distribution)
            if(updateMetadata.isForced){
                UFState(UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION, updatedData)
            } else {
                UFState(UFState.Name.SAVING_FILE, updatedData)
            }
        } else {
            state
        }
    }
}