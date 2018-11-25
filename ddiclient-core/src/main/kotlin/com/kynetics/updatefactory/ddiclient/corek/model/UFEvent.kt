/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model

import com.kynetics.redux.api.Action
import com.kynetics.updatefactory.ddiclient.core.model.Hash
import com.kynetics.updatefactory.ddiclient.corek.model.UFState.Distribution

/**
 * @author Daniele Sergio
 */
data class UFEvent<P>(override val name: Name, override val payload: P) : Action<P> {

    enum class Name{
        NO_ACTION_FOUND,
        ACTION_FOUND,
        COMMUNICATION_ERROR,
        UPDATE_INITIALIZED,
        FILE_CORRUPTED,
        UPDATE_ERROR,
        UPDATE_SUCCESS,
        UPDATE_STATUS_SEND,
        AUTHORIZATION_GRANTED,
        AUTHORIZATION_DENIED,
        RESUME,
        FORCE_CANCEL,
        FILE_SAVED,
        UPDATE_CANCELLED
    }

    enum class ActionType{
        UPDATE_DEVICE_METADATA,
        CANCEL_UPDATE,
        NEW_UPDATE
    }

    companion object {
        val newAuthorizationGrantedEvent = UFEvent(Name.AUTHORIZATION_GRANTED, Unit)
        val newAuthorizationDeniedEvent = UFEvent(Name.AUTHORIZATION_DENIED, Unit)
        val newUpdateSuccessEvent = UFEvent(Name.UPDATE_SUCCESS, Unit)
        val newUpdateStatusSendEvent = UFEvent(Name.UPDATE_STATUS_SEND, Unit)
        val newUpdateResumeEvent = UFEvent(Name.RESUME, Unit)
        val newUpdateCancelledEvent = UFEvent(Name.UPDATE_CANCELLED, Unit)
        val newForceCancelEvent = UFEvent(Name.FORCE_CANCEL, Unit)
        val newFileSavedEvent = UFEvent(Name.FILE_SAVED, Unit)
        val newFileCorruptedlEvent : (Pair<String, Hash>) -> UFEvent<Pair<String,Hash>> = { fileNameWithHash -> UFEvent(Name.FILE_CORRUPTED, fileNameWithHash)}
        val newUpdateErrorEvent : (Error) -> UFEvent<Error> = { error -> UFEvent(Name.UPDATE_ERROR, error)}
        val newCommunicationErrorEvent : (Error) -> UFEvent<Error> = { error -> UFEvent(Name.COMMUNICATION_ERROR, error)}
        val newNoActionFoundEvent: (Long) -> UFEvent<Long> = {sleepTime -> UFEvent(Name.NO_ACTION_FOUND, sleepTime) }
        val newActionsFoundEvent: (Map<ActionType,Long>) -> UFEvent<Map<ActionType,Long>> = { actions -> UFEvent(Name.ACTION_FOUND, actions) }
        val newUpdateInitializedEvent: (UpdateMetadata) -> UFEvent<UpdateMetadata> = { updateMetadata -> UFEvent(Name.UPDATE_INITIALIZED, updateMetadata) }
    }

    data class UpdateMetadata(val distribution: Distribution, val isDownloadForced: Boolean, val isUpdateForced: Boolean)
}