/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.redux.api.DispatcherType
import com.kynetics.redux.api.Middleware
import com.kynetics.redux.api.MiddlewareApi
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import org.slf4j.LoggerFactory

/**
 * @author Daniele Sergio
 */

abstract class AbstractRestApiMiddleware(vararg  val conditionToApplyMiddleware: Pair<UFState.Name,UFEvent.Name>):Middleware<UFState,UFEvent<*>,UFEvent<*>,UFEvent<*>,UFEvent<*>>{

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractRestApiMiddleware::class.java)
    }

    override fun apply(middlewareApi: MiddlewareApi<UFState, UFEvent<*>, UFEvent<*>>): (DispatcherType<UFEvent<*>, UFEvent<*>>) -> DispatcherType<UFEvent<*>, UFEvent<*>> {
        return {dispacher ->
            { action ->
                var actionToSend = action
                val currentState = middlewareApi.getState()
                val valueToTest = Pair(currentState.name, action.name)
                if(valueToTest in conditionToApplyMiddleware){
                    if(LOGGER.isDebugEnabled){
                        LOGGER.debug("Calling rest api with action [${action}")
                    }
                    actionToSend = callRestApi(currentState, action)
                    if(LOGGER.isDebugEnabled && actionToSend != action){
                        LOGGER.debug("Action has updated [${action}] -> [${actionToSend}]")
                    }
                }
                dispacher.invoke(actionToSend)
            }
        }
    }

    protected abstract fun callRestApi(state:UFState, action:UFEvent<*>):UFEvent<*>

}