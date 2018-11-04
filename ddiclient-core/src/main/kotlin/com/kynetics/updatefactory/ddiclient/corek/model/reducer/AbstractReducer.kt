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
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent.Name.*
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import org.slf4j.LoggerFactory

/**
 * @author Daniele Sergio
 */
abstract class AbstractReducer(vararg val stateToReduces: UFState.Name): Reducer<UFState, UFEvent<*>> {
    final override fun reduce(state: UFState, action: UFEvent<*>): UFState {
        return if(state.name in stateToReduces) state else _reduce(state, action)
    }

    abstract protected fun _reduce(state: UFState, action: UFEvent<*>): UFState

    companion object {
        protected val LOGGER = LoggerFactory.getLogger(AbstractReducer::class.java)
    }
}