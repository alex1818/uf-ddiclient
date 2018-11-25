/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek

import com.kynetics.redux.Utils
import com.kynetics.redux.api.EnhancerType
import com.kynetics.redux.api.Store
import com.kynetics.updatefactory.ddiclient.core.UFService
import com.kynetics.updatefactory.ddiclient.core.servicecallback.SystemOperation
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.middleware.*
import com.kynetics.updatefactory.ddiclient.corek.model.reducer.*
import com.kynetics.updatefactory.ddiclient.corek.timertask.PollingTask
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread

/**
 * @author Daniele Sergio
 */
class UFService(
        private val client:Client,
        private val systemOperation: SystemOperation,
        private val targetData: UFService.TargetData,
        private val initialState: UFState = UFState(UFState.Name.WAITING, UFState.Data(30_000L))) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UFService::class.java)
    }

    private val eventPublisher: EventPublisher
    private val timer = Timer()
    private var pollingTask: PollingTask
    private val store : Store<UFState, UFEvent<*>, UFEvent<*>>
    private val eventQueue: ArrayBlockingQueue<UFEvent<*>> = ArrayBlockingQueue(1)

    init {
        eventPublisher = object : EventPublisher {
            override fun publishEvent(event: UFEvent<*>) {
                eventQueue.put(event)
            }
        }
        val reducer = Utils.combineReducers(
                ApplyingSoftwareModuleReducer()::reduce,
                SavingFileReducer()::reduce,
                SendingUpdateResultReducer()::reduce,
                UpdateInitializationReducer()::reduce,
                WaitingAuthorizationReducer()::reduce,
                WaitingReducer()::reduce
        )
        val enhancer : EnhancerType<UFState, UFEvent<*>, UFEvent<*>, UFEvent<*>, UFEvent<*>> = Utils.applyMiddleware(
                ApplyingSoftwareModuleMiddleware(eventPublisher,systemOperation)::apply,
                CancelUpdateMiddleware(client, eventPublisher)::apply,
                SavingFileMiddleware(client, eventPublisher, systemOperation)::apply,
                SendUpdateResuntMiddleware(client, eventPublisher)::apply,
                UpdateIntializationMiddleware(client, eventPublisher)::apply,
                UpdateTargetDataMiddleware(client,targetData)::apply
        )

        store = Utils.createStore(reducer,initialState,enhancer)
        pollingTask = PollingTask(client, eventPublisher)
        store.subscribe{ oldState, newState ->
            if(handlePollingOnCommunicationErrorStartOrFinish(oldState, newState)){
                return@subscribe
            }
            if(handlePollingOnSleepTimeChange(oldState,newState)){
                return@subscribe
            }
        }

    }

    private fun handlePollingOnCommunicationErrorStartOrFinish(oldState: UFState, newState: UFState): Boolean {
        if (oldState.name == newState.name) {
            return false
        }
        var handle = false
        if (newState.name == UFState.Name.COMMUNICATION_ERROR) {
            updatePolling(30_000L)
            handle = true
        } else if (oldState.name == UFState.Name.COMMUNICATION_ERROR) {
            updatePolling(newState.data.sleepTime)
            handle = true
        }
        return handle
    }

    private fun handlePollingOnSleepTimeChange(oldState: UFState, newState: UFState): Boolean {
        val newSleepTime = newState.data.sleepTime
        if (oldState.data.sleepTime != newSleepTime) {
            updatePolling(newSleepTime)
            return true
        }
        return false
    }

    private fun stopPolling(){
        pollingTask.cancel()
        pollingTask = PollingTask(client, eventPublisher)
    }

    private fun updatePolling(period:Long){
        stopPolling()
        timer.scheduleAtFixedRate(this.pollingTask, period, Math.max(period,30_000L))
    }

    fun start(){
        updatePolling(store.getState().data.sleepTime)
        thread(start = true) {
            LOGGER.info("UFService started")
            while(true){
                val event = eventQueue.take()
                store.dispatch(event)
            }
        }
    }
}