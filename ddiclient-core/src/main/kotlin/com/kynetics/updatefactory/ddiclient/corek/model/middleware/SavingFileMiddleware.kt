/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.updatefactory.ddiclient.core.filterinputstream.CheckFilterInputStream
import com.kynetics.updatefactory.ddiclient.core.servicecallback.SystemOperation
import com.kynetics.updatefactory.ddiclient.corek.Client
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent.Companion.newFileCorruptedlEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent.Companion.newFileSavedEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.EventPublisherCallback
import okhttp3.ResponseBody

/**
 * @author Daniele Sergio
 */

class SavingFileMiddleware(val client: Client, val eventPublisher: EventPublisher, val systemOperation: SystemOperation): AbstractRestApiMiddleware(
        Pair(UFState.Name.SAVING_FILE,UFEvent.Name.FILE_SAVED),
        Pair(UFState.Name.WAITING_DOWNLOAD_AUTHORIZATION,UFEvent.Name.AUTHORIZATION_GRANTED),
        Pair(UFState.Name.UPDATE_INITIALIZATION,UFEvent.Name.UPDATE_INITIALIZED)){
    override fun callRestApi(state: UFState, action: UFEvent<*>): UFEvent<*>{
        action as UFEvent<UFEvent.UpdateMetadata>

        //todo check null value
        val currentSoftwareModule = if (state.name == UFState.Name.SAVING_FILE) state.data.distribution!!.nextStep(true).getCurrentSoftwareModule() else action.payload.distribution.getCurrentSoftwareModule()
        val currentFileInfo = currentSoftwareModule.getCurrentFileInfo()
        client.downloadArtifact(currentSoftwareModule.id, currentFileInfo.linkInfo.fileName)
                .enqueue(object : EventPublisherCallback<ResponseBody>(eventPublisher){
                    override fun onSuccess(response: ResponseBody) {
                        super.onSuccess(response)
                        val streamWithChecker = CheckFilterInputStream.builder()
                                .withStream(response.byteStream())
                                .withMd5Value(currentFileInfo.hash.md5)
                                .withSha1Value(currentFileInfo.hash.sha1)
                                .withListener { isValid, hash ->
                                    if(isValid){
                                        eventPublisher.publishEvent(newFileSavedEvent)
                                    } else {
                                        eventPublisher.publishEvent(newFileCorruptedlEvent(Pair(currentFileInfo.linkInfo.fileName, hash)))
                                    }

                                }
                                .build()

                        //todo add NotifyStatusFilterInputStream and use coroutine

                        Thread {
                            systemOperation.savingFile(streamWithChecker, currentFileInfo)
                        }.start()
                    }
                })


        return action
    }



}