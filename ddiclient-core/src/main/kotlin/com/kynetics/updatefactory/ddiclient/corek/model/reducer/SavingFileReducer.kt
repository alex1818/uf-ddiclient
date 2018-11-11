/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.reducer

import com.kynetics.updatefactory.ddiclient.core.model.Hash
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState

/**
 * @author Daniele Sergio
 */
class SavingFileReducer : AbstractReducer(UFState.Name.SAVING_FILE) {
    override fun _reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(action.name){
            UFEvent.Name.FILE_SAVED     -> getNextStateOnFileSaved(state)
            UFEvent.Name.FILE_CORRUPTED -> getNextStateOnFileCorrupted(state, (action as UFEvent<Pair<String,Hash>>).payload.second)
            else                        -> state
        }
    }

    private fun getNextStateOnFileSaved(state: UFState): UFState {
        return if (state.data.distribution!!.isSoftwareModuleDownloaded()) {
            getNextStateOnFinshDownload(state)
        } else {
            UFState(UFState.Name.SAVING_FILE, state.data.copy(distribution = state.data.distribution.nextStep(true))) //todo fix nextStep
        }
    }


    private fun getNextStateOnFinshDownload(state: UFState):UFState{
        return if(state.data.isForced!!) UFState(UFState.Name.APPLYING_SOFTWARE_MODULE, state.data.copy(updateStarted = true)) else
            UFState(UFState.Name.WAITING_UPDATE_AUTHORIZATION, state.data)
    }

    private fun getNextStateOnFileCorrupted(state:UFState, hash: Hash):UFState{
        val data = state.data
        if(data.savingFile!!.lastHash == hash){
            return UFState(UFState.Name.SENDING_UPDATE_STATUS, data.copy(updateResponse = UFState.UpdateResponse(false, arrayOf("File corrupted"))))
        }
        val remainingAttempts = state.data.savingFile!!.remainingAttempts
        return if (remainingAttempts > 0){
            state.copy(data = data.copy(savingFile = UFState.SavingFile(hash, remainingAttempts - 1)))
        } else state
    }
}