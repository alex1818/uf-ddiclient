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

import com.kynetics.updatefactory.ddiclient.core.model.event.AbstractEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.AbstractState.StateName.SERVER_FILE_CORRUPTED;

/**
 * @author Daniele Sergio
 */
public class ServerFileCorruptedState extends AbstractStateWithAction {

    private static final long serialVersionUID = 3171662012367375837L;

    public ServerFileCorruptedState(Long actionId) {
        super(SERVER_FILE_CORRUPTED, actionId);
    }

    @Override
    public AbstractState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SUCCESS:
                return new WaitingState(0, null);
            default:
                return super.onEvent(event);
        }
    }
}
