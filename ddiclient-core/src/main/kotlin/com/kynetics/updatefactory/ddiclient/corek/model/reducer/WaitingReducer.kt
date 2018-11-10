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
class WaitingReducer : AbstractReducer(UFState.Name.WAITING) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(action.name){
            UFEvent.Name.NO_ACTION_FOUND ->  getNextStateOnNoActionFound(action, state)
            UFEvent.Name.RESUME          -> UFState(state.data.proxyState!!.name, state.data.copy(proxyState = null))
            UFEvent.Name.ACTION_FOUND    -> getNextStateOnActionsFound(action, state)
            else                         -> state
        }
    }

    private fun getNextStateOnActionsFound(action: UFEvent<*>, state: UFState): UFState {
        action as UFEvent<Map<UFEvent.ActionType, Long>>
        return if (action.payload.containsKey(UFEvent.ActionType.NEW_UPDATE)) {
            UFState(UFState.Name.UPDATE_INITIALIZATION, state.data.copy(actionId = action.payload[UFEvent.ActionType.NEW_UPDATE]!!))
        } else {
            state
        }
    }

    private fun getNextStateOnNoActionFound(action: UFEvent<*>, state: UFState): UFState {
        action as UFEvent<Long>
        return UFState(name = UFState.Name.WAITING,
                data = state.data.copy(sleepTime = action.payload, actionId = -1))
    }
}