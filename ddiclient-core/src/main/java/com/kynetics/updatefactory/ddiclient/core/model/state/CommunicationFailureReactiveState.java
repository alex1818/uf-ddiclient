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

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.COMMUNICATION_FAILURE;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class CommunicationFailureReactiveState extends AbstractCommunicationReactiveState {
    // TODO: 10/24/18 merge this class with CommunicationErrorReactiveState
    private CommunicationFailureReactiveState(State state, int attemptsRemaining) {
        super(state, attemptsRemaining);
    }

    public static CommunicationFailureReactiveState newInstance(ReactiveState previousState, Error error, int attemptsRemaining) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(COMMUNICATION_FAILURE)
                .withError(error)
                .withPreviousState(previousState);
        return new CommunicationFailureReactiveState(stateBuilder.build(), attemptsRemaining);
    }
}
