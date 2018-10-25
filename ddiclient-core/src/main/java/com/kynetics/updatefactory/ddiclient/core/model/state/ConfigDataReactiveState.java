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

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.CONFIG_DATA;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class ConfigDataReactiveState extends AbstractReactiveState {
    private ConfigDataReactiveState(State state) {
        super(state);
    }

    public static ConfigDataReactiveState newInstance(ReactiveState previousState) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(CONFIG_DATA)
                .withPreviousState(null);
        return new ConfigDataReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SUCCESS:
                return WaitingReactiveState.fromPreviousState(this, 0); // todo before refactor: return new WaitingReactiveState(0, null);
            default:
                return super.onEvent(event);
        }
    }
}
