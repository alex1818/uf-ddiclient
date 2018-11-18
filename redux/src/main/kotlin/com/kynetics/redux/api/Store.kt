/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.redux.api

import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

/**
 * @author Daniele Sergio
 */
interface Store<S: State<*>, A: Action<*>, R>: MiddlewareApi<S, A, R> {

    fun subscribe(listener: Subscription<S>): UnSubscription

    fun replaceReducer(nextReducer: ReducerType<S, A>)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Store::class.java)

        fun <S: State<*>, A: Action<*>>create(reducerType: ReducerType<S, A>, initialState:S): Store<S,A,A> {
            return object : Store<S, A, A> {

                private var reducer = reducerType

                private val subscriptions = ArrayList<(S, S) -> Unit>()

                private var s by Delegates.observable(initialState) { property, oldValue, newValue ->
                    if(LOGGER.isInfoEnabled && oldValue != newValue){
                        LOGGER.info("State has changed: [$oldValue] -> [$newValue]")
                    }
                    subscriptions.forEach{
                        ele -> ele.invoke(oldValue, newValue)
                    }
                }

                override fun dispatch(action: A): A {
                    if(LOGGER.isInfoEnabled) {
                        LOGGER.info("Dispatching action: [${action}]")
                    }
                    s = reducer.invoke(s, action)
                    return action
                }

                override fun getState(): S {
                    return s
                }

                override fun subscribe(listener: Subscription<S>): UnSubscription {
                    subscriptions.add(listener)
                    return { subscriptions.remove(listener)}
                }

                override fun replaceReducer(nextReducer: ReducerType<S, A>) {
                    reducer = nextReducer
                }
            }
        }

    }
}

