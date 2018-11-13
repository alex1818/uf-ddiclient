/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek

import com.kynetics.updatefactory.ddiclient.api.api.DdiRestApi
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiActionFeedback
import com.kynetics.updatefactory.ddiclient.api.model.request.DdiConfigData
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiArtifact
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiCancel
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiControllerBase
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiDeploymentBase
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * @author Daniele Sergio
 */

class Client(val api:DdiRestApi, val tenant:String, val controllerId:String){
    fun getSoftwareModulesArtifacts(softwareModuleId: Long?): Call<MutableList<DdiArtifact>> {
        return api.getSoftwareModulesArtifacts(tenant,controllerId,softwareModuleId)
    }

    fun getControllerBase(): Call<DdiControllerBase> {
        return api.getControllerBase(tenant, controllerId)
    }

    fun downloadArtifact(softwareModuleId: Long?, fileName: String?): Call<ResponseBody> {
        return api.downloadArtifact(tenant,controllerId,softwareModuleId, fileName)
    }

    fun getControllerBasedeploymentAction(actionId: Long?, resource: Int, actionHistoryMessageCount: Int?): Call<DdiDeploymentBase> {
        return api.getControllerBasedeploymentAction(tenant,controllerId,actionId, resource, actionHistoryMessageCount)
    }

    fun postBasedeploymentActionFeedback(actionId: Long?, feedback: DdiActionFeedback?): Call<Void> {
        return api.postBasedeploymentActionFeedback(tenant,controllerId,actionId,feedback)
    }

    fun putConfigData(configData: DdiConfigData?): Call<Void> {
        return api.putConfigData(tenant,controllerId,configData)
    }

    fun getControllerCancelAction(actionId: Long?): Call<DdiCancel> {
        return api.getControllerCancelAction(tenant,controllerId,actionId)
    }

    fun postCancelActionFeedback(actionId: Long?, feedback: DdiActionFeedback?): Call<Void> {
        return api.postCancelActionFeedback(tenant,controllerId,actionId,feedback)
    }
}