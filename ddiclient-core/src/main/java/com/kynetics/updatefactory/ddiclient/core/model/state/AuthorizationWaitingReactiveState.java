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
import com.kynetics.updatefactory.ddiclient.core.model.event.CancelEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.AUTHORIZATION_WAITING;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class AuthorizationWaitingReactiveState extends AbstractReactiveState {

    private transient boolean requestSend;

    private AuthorizationWaitingReactiveState(State state) {
        super(state);
    }

    public static AuthorizationWaitingReactiveState newInstance(ReactiveState previousState, long actionId) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(AUTHORIZATION_WAITING)
                .withPreviousState(previousState)
                .withActionId(actionId);
        return new AuthorizationWaitingReactiveState(stateBuilder.build());
    }


    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case AUTHORIZATION_PENDING:
                return this;
            case AUTHORIZATION_GRANTED:
                return getPreviousState();
            case CANCEL:
                return CancellationCheckReactiveState.newInstance(this, ((CancelEvent) event).getActionId());
            case AUTHORIZATION_DENIED:
                return WaitingReactiveState.fromPreviousState(getPreviousState(), 30_000, true);
            default:
                return super.onEvent(event);
        }
    }

}
