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
import com.kynetics.updatefactory.ddiclient.core.model.event.SleepEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.UpdateFoundEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.*;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class WaitingReactiveState extends AbstractReactiveState {

    private WaitingReactiveState(State state) {
        super(state);
    }

    public static WaitingReactiveState fromPreviousState(ReactiveState previousState, long sleepTime, boolean storePreviousState) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(WAITING)
                .withPreviousState(storePreviousState ? previousState : null)
                .withSleepTime(sleepTime);
        return new WaitingReactiveState(stateBuilder.build());
    }

    public static WaitingReactiveState fromPreviousState(ReactiveState previousState, long sleepTime) {
        return fromPreviousState(previousState, sleepTime, false);
    }

    public static WaitingReactiveState newInstance(long sleepTime) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder()
                .withStateName(WAITING)
                .withSleepTime(sleepTime);
        return new WaitingReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SLEEP_REQUEST:
                return hasInnerState() ?
                        fromPreviousState(getPreviousState(), ((SleepEvent) event).getSleepTime(), hasInnerState()) :
                        newInstance(((SleepEvent) event).getSleepTime());
            case UPDATE_CONFIG_REQUEST:
                return ConfigDataReactiveState.newInstance(this);
            case UPDATE_FOUND:
                final UpdateFoundEvent updateFoundEvent = (UpdateFoundEvent) event;
                return hasInnerState() && updateFoundEvent.getActionId().equals(getInnerStateActionId()) ?
                        this :
                        UpdateInitialization.newInstance(this, ((UpdateFoundEvent) event).getActionId());
            case CANCEL:
                return CancellationCheckReactiveState.newInstance(this, ((CancelEvent) event).getActionId());
            case RESUME:
                return innerStateIsCommunicationState() ?
                        getMostInnerState() :
                        AuthorizationWaitingReactiveState.newInstance(getPreviousState(), getActionId());
            default:
                return super.onEvent(event);
        }

    }

    public boolean innerStateIsCommunicationState() {
        if (!hasInnerState()) {
            return false;
        }
        final StateName innerStateName = getPreviousState().getStateName();
        return innerStateName == COMMUNICATION_ERROR || innerStateName == COMMUNICATION_FAILURE;
    }

    private ReactiveState getMostInnerState() {
        return innerStateIsCommunicationState() ? (getPreviousState()).getPreviousState() : getPreviousState();
    }

    private Long getInnerStateActionId() {
        final long actionId = getMostInnerState().getActionId();
        return actionId >= 0 ? actionId : null;
    }
//    private Long getInnerStateActionId() {
//        return getMostInnerState() instanceof AbstractReactiveStateWithAction ?
//                ((AbstractReactiveStateWithAction) state).getActionId() :
//                null;
//    }
}
