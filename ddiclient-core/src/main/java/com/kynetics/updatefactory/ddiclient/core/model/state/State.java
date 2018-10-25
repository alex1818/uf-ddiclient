/*
 * Copyright © 2017-2018 Kynetics LLC
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 */

package com.kynetics.updatefactory.ddiclient.core.model.state;

import com.kynetics.updatefactory.ddiclient.core.model.Distribution;
import com.kynetics.updatefactory.ddiclient.core.model.FileInfo;
import com.kynetics.updatefactory.ddiclient.core.model.Hash;

import java.io.InputStream;
import java.util.List;

/**
 * @author Daniele Sergio
 */
public interface State {
    enum StateName{
        WAITING, CONFIG_DATA, UPDATE_INITIALIZATION, UPDATE_DOWNLOAD, SAVING_FILE, UPDATE_READY, UPDATE_STARTED, CANCELLATION_CHECK,
        CANCELLATION, UPDATE_ENDED, COMMUNICATION_FAILURE, COMMUNICATION_ERROR, AUTHORIZATION_WAITING, SERVER_FILE_CORRUPTED
    }

    interface UpdateResponse {
        boolean isSuccessfullyUpdate();
        String[] getDetails();
    }

    interface Error{
        long getCode();
        String[] getDetails();
        Throwable getThrowable();
    }

    long getSleepTime();

    StateName getStateName();
    long getActionId();
    State getPreviousState(); //previousState/ innerState
    boolean isForced();

    //updateEnded
    UpdateResponse getUpdateResponse();

    //abstractStateWithFile
    Distribution getDistribution();
    Hash getLastHash();

    //communicationErrorState
    Error getError();

    //savingFile
    InputStream getInputStream();
    boolean isInputStreamAvailable();
    double getPercent();


    boolean hasInnerState();
}
