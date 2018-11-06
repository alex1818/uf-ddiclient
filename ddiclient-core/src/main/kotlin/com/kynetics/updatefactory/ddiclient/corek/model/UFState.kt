/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model

import com.kynetics.redux.api.State
import com.kynetics.updatefactory.ddiclient.core.model.FileInfo
import com.kynetics.updatefactory.ddiclient.core.model.Hash
import java.io.InputStream
import java.util.Comparator

/**
 * @author Daniele Sergio
 */
data class UFState(override val name: Name, override val data: Data) : State<UFState.Data> {

    enum class Name {
        WAITING,
        UPDATE_INITIALIZATION, //GET UPDATE METADATA
        WAITING_DOWNLOAD_AUTHORIZATION,
        SAVING_FILE, //DOWNLOADING/STORING A FILE
//        UPDATE_READY,  // ALL FILE DOWNLOADED
        WAITING_UPDATE_AUTHORIZATION,
        APPLYING_UPDATE, // UPDATE STARTED
        SENDING_UPDATE_STATUS, // UPDATE ENDED
        UPDATE_CANCELLED,
        COMMUNICATION_ERROR
    }

    data class Data(val sleepTime: Long,
                    val stateName: com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName,
                    val actionId: Long,
                    val isForced: Boolean,
            //updateEnded
                    val updateResponse: UpdateResponse,
            //abstractStateWithFile
                    val distribution: Distribution,
                    val lastHash: Hash,
            //communicationErrorState
                    val error: Error,
            //savingFile
                    val savingFile: SavingFile,
            //suspend/authorization
                    val proxyState: ProxyState
    )

    data class ProxyState(val name: Name, val actionId:Long)

    data class SavingFile(
            val inputStream: InputStream,
            val isInputStreamAvailable: Boolean, //TODO override getinputstream
            val percent: Double
    )

    data class UpdateResponse(
            val isSuccessfullyUpdate: Boolean,
            val details: Array<String>
    )

    data class Distribution(val softwareModules: Array<SoftwareModule>, val currentSoftwareModuleIndex: Int = 0, val error: Boolean = false) {

        fun nextStep(currentSoftwareModuleSuccessfullyApplied: Boolean): Distribution {
            val flag = !currentSoftwareModuleSuccessfullyApplied || error
            if (!softwareModules[currentSoftwareModuleIndex].currentFileIsLast()) {
                val softwareModulesUpdated = softwareModules.toMutableList()
                softwareModulesUpdated.set(currentSoftwareModuleIndex, softwareModules[currentSoftwareModuleIndex].nextStep())
                return copy(softwareModules =  softwareModulesUpdated.toTypedArray(), error = flag)
            }
            return if (hasNextSoftwareModule()) copy(currentSoftwareModuleIndex =  currentSoftwareModuleIndex + 1, error = flag) else this
        }


        fun hasNextSoftwareModule(): Boolean {
            return softwareModules.size == currentSoftwareModuleIndex
        }

        // TODO: 10/25/18 use comparator when construct object
        private class SoftwareModuleComparatorByType : Comparator<SoftwareModule> {
            override fun compare(s1: SoftwareModule, s2: SoftwareModule): Int {
                if (s1.type == s2.type) {
                    return 0
                }
                return if (s1.type == SoftwareModule.Type.APP) -1 else 1
            }
        }
    }

    data class SoftwareModule(val type: Type, val id: Long, val files: Array<FileInfo>, val currentFileIndex: Int = 0) {

        enum class Type {
            OS, APP
        }

        fun getCurrentFileInfo(): FileInfo {
            return files[currentFileIndex]
        }


        fun nextStep(): SoftwareModule {
            return if (currentFileIsLast()) this else copy(currentFileIndex = currentFileIndex - 1)
        }

        fun currentFileIsLast(): Boolean {
            return currentFileIndex == files.size
        }

    }
}