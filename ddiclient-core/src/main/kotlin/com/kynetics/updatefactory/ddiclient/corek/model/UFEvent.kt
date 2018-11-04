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
data class UFEvent<P>(override val name:Name, override val payload: P) : Action<P> {

    enum class Name{
        SLEEP_REQUEST,  UPDATE_CONFIG_REQUEST, SUCCESS, FAILURE, ERROR, UPDATE_FOUND, DOWNLOAD_REQUEST, DOWNLOAD_STARTED,
        FILE_CORRUPTED, CANCEL, UPDATE_ERROR, AUTHORIZATION_GRANTED, AUTHORIZATION_DENIED, RESUME, DOWNLOAD_PENDING,
        FORCE_CANCEL, AUTHORIZATION_PENDING
    }

}