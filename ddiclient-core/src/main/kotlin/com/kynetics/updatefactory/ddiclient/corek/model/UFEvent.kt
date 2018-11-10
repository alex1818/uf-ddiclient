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
/**
 * @author Daniele Sergio
 */
data class UFEvent<P>(override val name: Name, override val payload: P) : Action<P> {

    enum class Name{
        NO_ACTION_FOUND,//SLEEP_REQUEST,
        ACTION_FOUND,//UPDATE_CONFIG_REQUEST, NEW_UPDATE, CANCELL_UPDATE //
//        SUCCESS,
        //FAILURE,
        COMMUNICATION_ERROR,
//        UPDATE_FOUND,
//        DOWNLOAD_REQUEST,
//        DOWNLOAD_STARTED,
        UPDATE_INITIALIZED,
        FILE_CORRUPTED,
//        CANCEL,
        UPDATE_ERROR,
        UPDATE_SUCCESS,
        UPDATE_STATUS_SEND,
        AUTHORIZATION_GRANTED,
        AUTHORIZATION_DENIED,
        RESUME,
        FORCE_CANCEL,
        FILE_SAVED
    }

    enum class Action{
        UPDATE_DEVICE_METADATA,
        CANCEL_UPDATE,
        NEW_UPDATE
    }

    companion object {
        val newAuthorizationGrantedEvent = UFEvent(Name.AUTHORIZATION_GRANTED, Unit)
        val newAuthorizationDeniedEvent = UFEvent(Name.AUTHORIZATION_DENIED, Unit)
        val newUpdateSuccessEvent = UFEvent(Name.UPDATE_SUCCESS, Unit)
        val newUpdateResumeEvent = UFEvent(Name.RESUME, Unit)
        val newForceCancelEvent = UFEvent(Name.FORCE_CANCEL, Unit)
        val newFileCorruptedlEvent : (Pair<String,String>) -> UFEvent<Pair<String,String>> = {fileNameWithHash -> UFEvent(Name.FILE_CORRUPTED, fileNameWithHash)}
        val newUpdateErrorEvent : (Error) -> UFEvent<Error> = { error -> UFEvent(Name.UPDATE_ERROR, error)}
        val newCommunicationErrorEvent : (Error) -> UFEvent<Error> = { error -> UFEvent(Name.COMMUNICATION_ERROR, error)}
        val newNoActionEventFound: (Long) -> UFEvent<Long> = {sleepTime -> UFEvent(Name.NO_ACTION_FOUND, sleepTime) }
        val newActionFoundEventFound: (Map<Action,Long>) -> UFEvent<Map<Action,Long>> = { actions -> UFEvent(Name.ACTION_FOUND, actions) }
    }


}