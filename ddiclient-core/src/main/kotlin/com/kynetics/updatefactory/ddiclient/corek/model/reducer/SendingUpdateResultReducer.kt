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
import com.kynetics.updatefactory.ddiclient.corek.model.UFState.Companion.getNewUFState

/**
 * @author Daniele Sergio
 */
class SendingUpdateResultReducer : AbstractReducer(UFState.Name.SENDING_UPDATE_STATUS) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(action.name){
            UFEvent.Name.UPDATE_STATUS_SEND -> getNewUFState(state.data.sleepTime)
            else                            -> state
        }
    }
}