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
import com.kynetics.updatefactory.ddiclient.core.model.event.ErrorEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.FailureEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.UPDATE_DOWNLOAD;

/**
 * @author Daniele Sergio
 */
public abstract class AbstractCommunicationReactiveState extends AbstractReactiveState {

    static final int MAX_ATTEMPTS = 5;

    private final int attemptsRemaining;

     AbstractCommunicationReactiveState(State state, int attemptsRemaining) {
        super(state);
        if (state != null && state.getStateName().equals(UPDATE_DOWNLOAD)) {
            this.attemptsRemaining = attemptsRemaining;
        } else {
            this.attemptsRemaining = MAX_ATTEMPTS;
        }
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case ERROR:
                final ErrorEvent errorEvent = (ErrorEvent) event;
                return attemptsRemaining == 0 ? WaitingReactiveState.fromPreviousState(this, 0, true) : getStateOnError(errorEvent, getPreviousState(), attemptsRemaining - 1);
            case FAILURE:
                FailureEvent failureEvent = (FailureEvent) event;
                return attemptsRemaining == 0 ? WaitingReactiveState.fromPreviousState(this, 0, true) : CommunicationFailureReactiveState.newInstance(getPreviousState(), new StateImpl.ErrorImpl(-1, new String[0], failureEvent.getThrowable()), attemptsRemaining - 1);
            default:
                return getPreviousState().onEvent(event);
        }
    }
}
