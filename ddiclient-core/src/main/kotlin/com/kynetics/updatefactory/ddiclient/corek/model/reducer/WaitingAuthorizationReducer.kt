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
class WaitingAuthorizationReducer : AbstractReducer(UFState.Name.WAITING_UPDATE_AUTHORIZATION, UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(action.name){
            UFEvent.Name.AUTHORIZATION_DENIED  -> UFState(UFState.Name.WAITING, state.data.copy(proxyState = UFState.ProxyState(state.name, state.data.actionId!!)))
            UFEvent.Name.AUTHORIZATION_GRANTED -> getNextStateOnAuthorizationGranted(state)
            else                               -> state
        }
    }

    private fun getNextStateOnAuthorizationGranted(state: UFState): UFState {
        return if(state.name == UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION) state.copy(name = UFState.Name.SAVING_FILE)
        else state.copy(name = UFState.Name.APPLYING_SOFTWARE_MODULE)
    }


}