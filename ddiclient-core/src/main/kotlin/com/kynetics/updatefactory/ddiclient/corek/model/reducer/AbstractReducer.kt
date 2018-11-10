/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.reducer

import com.kynetics.redux.api.Reducer
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent.Name.*
import com.kynetics.updatefactory.ddiclient.corek.model.*
import org.slf4j.LoggerFactory

/**
 * @author Daniele Sergio
 */
abstract class AbstractReducer(vararg val stateToReduces: UFState.Name): Reducer<UFState, UFEvent<*>> {

    init{
        check(!stateHandled.intersect(stateToReduces.toList()).isEmpty()) {"Some states have been already handled by other Reducer"}
        stateHandled.addAll(stateToReduces.toList())
    }

    final override fun reduce(state: UFState, action: UFEvent<*>): UFState {
        return when(state.name){
            COMMUNICATION_ERROR -> getNextStateOnCommunicationError(action, state)
            else                -> if(state.name in stateToReduces) state else _reduce(state, action)
        }
    }

    private fun getNextStateOnCommunicationError(action: UFEvent<*>, state: UFState): UFState {
        action as UFEvent<Error>
        return UFState(UFState.Name.COMMUNICATION_ERROR, state.data.copy(error = action.payload))
    }

    abstract protected fun _reduce(state: UFState, action: UFEvent<*>): UFState

    companion object {
        protected val LOGGER = LoggerFactory.getLogger(AbstractReducer::class.java)
        private val stateHandled = ArrayList<UFState.Name>()
    }
}