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
import java.util.*

/**
 * @author Daniele Sergio
 */
data class UFState(override val name: Name, override val data: Data) : State<UFState.Data> {

    companion object {
        fun getNewUFState(sleepTime:Long) = UFState(Name.WAITING, Data(sleepTime))
    }
    enum class Name {
        WAITING,
        UPDATE_INITIALIZATION, //GET UPDATE METADATA
        WAITING_DOWNLOAD_AUTHORIZATION,
        SAVING_FILE, //DOWNLOADING/STORING A FILE
//        UPDATE_READY,  // ALL FILE DOWNLOADED
        WAITING_UPDATE_AUTHORIZATION,
        APPLYING_SOFTWARE_MODULE, // UPDATE STARTED
        SENDING_UPDATE_STATUS, // UPDATE ENDED
        CANCELLING_UPDATE,
        COMMUNICATION_ERROR
    }

    data class Data @JvmOverloads constructor(val sleepTime: Long = 30_000L,
                    val actionId: Long? = null,
                    val isDownloadForced: Boolean? = null,
                    val isUpdateForced: Boolean? = null,
            //updateEnded
                    val updateResponse: UpdateResponse? = null,
            //abstractStateWithFile
                    val distribution: Distribution? = null,
            //communicationErrorState
                    val error: Error? = null,
            //savingFile
                    val savingFile: SavingFile? = null,
            //suspend/authorization
                    val proxyState: ProxyState? = null,
                    val updateStarted: Boolean = false)

    data class ProxyState (val name: Name, val actionId:Long? = null)

    data class SavingFile(
           /* val inputStream: InputStream,
            val isInputStreamAvailable: Boolean, //TODO override getinputstream
            val percent: Double,*/
            val lastHash: Hash,
            val remainingAttempts: Int
    )

    data class UpdateResponse(
            val isSuccessfullyUpdate: Boolean,
            val details: Array<String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UpdateResponse

            if (isSuccessfullyUpdate != other.isSuccessfullyUpdate) return false
            if (!Arrays.equals(details, other.details)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = isSuccessfullyUpdate.hashCode()
            result = 31 * result + Arrays.hashCode(details)
            return result
        }
    }

    data class Distribution @JvmOverloads constructor (val softwareModules: Array<SoftwareModule>, val currentSoftwareModuleIndex: Int = 0, val error: Boolean = false) {

        fun nextStep(currentSoftwareModuleSuccessfullyApplied: Boolean): Distribution {
            val flag = !currentSoftwareModuleSuccessfullyApplied || error
            if (!softwareModules[currentSoftwareModuleIndex].currentFileIsLast()) {
                val softwareModulesUpdated = softwareModules.toMutableList()
                softwareModulesUpdated.set(currentSoftwareModuleIndex, softwareModules[currentSoftwareModuleIndex].nextStep())
                return copy(softwareModules =  softwareModulesUpdated.toTypedArray(), error = flag)
            }
            return if (hasNextSoftwareModule()) copy(currentSoftwareModuleIndex =  currentSoftwareModuleIndex + 1, error = flag) else this
        }

        fun getCurrentSoftwareModule():SoftwareModule{
            return softwareModules[currentSoftwareModuleIndex]
        }

        fun isSoftwareModuleDownloaded():Boolean{
            return softwareModules[currentSoftwareModuleIndex].currentFileIsLast()
        }

        fun hasNextSoftwareModule(): Boolean {
            return softwareModules.size == currentSoftwareModuleIndex
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Distribution

            if (!Arrays.equals(softwareModules, other.softwareModules)) return false
            if (currentSoftwareModuleIndex != other.currentSoftwareModuleIndex) return false
            if (error != other.error) return false

            return true
        }

        override fun hashCode(): Int {
            var result = Arrays.hashCode(softwareModules)
            result = 31 * result + currentSoftwareModuleIndex
            result = 31 * result + error.hashCode()
            return result
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

    data class SoftwareModule @JvmOverloads constructor (val type: Type, val id: Long, val files: Array<FileInfo>, val currentFileIndex: Int = 0) {

        enum class Type {
            OS, APP
        }

        fun getCurrentFileInfo(): FileInfo {
            return files[currentFileIndex]
        }


        fun nextStep(): SoftwareModule {
            return if (currentFileIsLast()) this else copy(currentFileIndex = currentFileIndex + 1)
        }

        fun currentFileIsLast(): Boolean {
            return currentFileIndex == files.size
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SoftwareModule

            if (type != other.type) return false
            if (id != other.id) return false
            if (!Arrays.equals(files, other.files)) return false
            if (currentFileIndex != other.currentFileIndex) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + id.hashCode()
            result = 31 * result + Arrays.hashCode(files)
            result = 31 * result + currentFileIndex
            return result
        }

    }
}