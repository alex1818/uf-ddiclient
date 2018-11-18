/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.middleware

import com.kynetics.updatefactory.ddiclient.api.model.response.DdiDeployment
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiDeploymentBase
import com.kynetics.updatefactory.ddiclient.core.model.FileInfo
import com.kynetics.updatefactory.ddiclient.core.model.Hash
import com.kynetics.updatefactory.ddiclient.corek.Client
import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher
import com.kynetics.updatefactory.ddiclient.corek.model.UFEvent
import com.kynetics.updatefactory.ddiclient.corek.model.UFState
import com.kynetics.updatefactory.ddiclient.corek.model.apicallback.EventPublisherCallback

/**
 * @author Daniele Sergio
 */

class UpdateIntializationMiddleware(val client: Client, val eventPublisher: EventPublisher): AbstractUFMiddleware(
        Pair(UFState.Name.WAITING,UFEvent.Name.ACTION_FOUND)){

    override fun execute(state: UFState, action: UFEvent<*>): UFEvent<*>{
        action as UFEvent<Map<UFEvent.ActionType,Long>>

        val actionId = action.payload[UFEvent.ActionType.NEW_UPDATE] ?: return action

        client.getControllerBasedeploymentAction(actionId).enqueue(object : EventPublisherCallback<DdiDeploymentBase>(eventPublisher){
            override fun onSuccess(response: DdiDeploymentBase) {
                super.onSuccess(response)
                val softwareModuleList = mutableListOf<UFState.SoftwareModule>() //todo refactor this loop
                for (chunk in response.deployment.chunks) {
                    val fileInfoList = ArrayList<FileInfo>(chunk.artifacts.size)
                    for (artifact in chunk.artifacts) {
                        fileInfoList.add(FileInfo(
                                artifact.getLink("download-http").parseLink2(),
                                Hash(artifact.hashes.md5,
                                        artifact.hashes.sha1),
                                artifact.size!!))
                    }
                    softwareModuleList.add(UFState.SoftwareModule(if (chunk.part == "bApp") UFState.SoftwareModule.Type.APP else UFState.SoftwareModule.Type.OS,
                            if(fileInfoList.size > 0 ) fileInfoList[0].linkInfo.softwareModules else -1,
                            fileInfoList.toTypedArray(),
                            0))
                }

                val updateMetadata = UFEvent.UpdateMetadata(UFState.Distribution(softwareModuleList.toTypedArray()),
                        response.deployment.download == DdiDeployment.HandlingType.FORCED,
                        response.deployment.update == DdiDeployment.HandlingType.FORCED)
                eventPublisher.publishEvent(UFEvent.newUpdateInitializedEvent.invoke(updateMetadata))
            }
        })


        return action //todo update action to go to CANCELLING_UPDATE state
    }

}


