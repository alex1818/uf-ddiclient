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

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.UPDATE_READY;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class UpdateReadyReactiveState extends AbstractReactiveState {

    private UpdateReadyReactiveState(State state) {
        super(state);
    }

    public static UpdateReadyReactiveState newInstance(ReactiveState previousState, long actionId, boolean isForced) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(UPDATE_READY)
                .withPreviousState(null)
                .withIsForced(isForced)
                .withActionId(actionId);
        return new UpdateReadyReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case CANCEL:
                return CancellationCheckReactiveState.newInstance(this, ((CancelEvent) event).getActionId());
            case SUCCESS:
                final ReactiveState state = UpdateStartedReactiveState.newInstance(this, getActionId());
                return isForced() ? state : AuthorizationWaitingReactiveState.newInstance(state, getActionId());
            default:
                return super.onEvent(event);
        }
    }
}
