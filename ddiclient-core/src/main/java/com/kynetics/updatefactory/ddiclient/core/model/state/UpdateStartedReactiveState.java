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
import com.kynetics.updatefactory.ddiclient.core.model.event.UpdateErrorEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.UPDATE_STARTED;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class UpdateStartedReactiveState extends AbstractReactiveState {

    private UpdateStartedReactiveState(State state) {
        super(state);
    }

    public static UpdateStartedReactiveState newInstance(ReactiveState previousState, long actionId) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(UPDATE_STARTED)
                .withPreviousState(null)
                .withActionId(actionId);
        return new UpdateStartedReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SUCCESS:
                return UpdateEndedReactiveState.newInstance(this, getActionId(), new StateImpl.UpdateResponseImpl(true, new String[0]));
            case UPDATE_ERROR:
                final UpdateErrorEvent errorEvent = (UpdateErrorEvent) event;
                return UpdateEndedReactiveState.newInstance(this, getActionId(), new StateImpl.UpdateResponseImpl(false, errorEvent.getDetails()));
            default:
                return super.onEvent(event);
        }
    }
}
