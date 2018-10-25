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
import com.kynetics.updatefactory.ddiclient.core.model.event.SuccessEvent;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.*;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class CancellationCheckReactiveState extends AbstractReactiveState {

    private CancellationCheckReactiveState(State state) {
        super(state);
    }

    public static CancellationCheckReactiveState newInstance(ReactiveState previousState, Long actionId) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withPreviousState(previousState)
                .withStateName(StateName.CANCELLATION_CHECK)
                .withActionId(actionId);
        return new CancellationCheckReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SUCCESS: //must cancel the action into the event (successEvent.getActionId()) but I need to send the feedback to the action inside the nextFileToDownload state (getAction);
                final StateName stateName = getPreviousState().getStateName();
                final SuccessEvent successEvent = (SuccessEvent) event;
                if (getStateName() == UPDATE_READY) {
                    final UpdateReadyReactiveState updateReadyState = (UpdateReadyReactiveState) getPreviousState();
                    return updateReadyState.getActionId() == successEvent.getActionId() ?
                            CancellationReactiveState.newInstance(this, getActionId()) :
                            updateReadyState.isForced() ?
                                    UpdateStartedReactiveState.newInstance(this, updateReadyState.getActionId()) :
                                    AuthorizationWaitingReactiveState.newInstance(updateReadyState, getActionId());
                } else if (stateName == SAVING_FILE){
                    final SavingFileReactiveState savingFileState = (SavingFileReactiveState) getPreviousState();
                    return  savingFileState.getActionId() == successEvent.getActionId() ?
                            CancellationReactiveState.newInstance(this, getActionId()) : savingFileState;
                } else if (stateName == AUTHORIZATION_WAITING){
                    final AuthorizationWaitingReactiveState authorizationWaitingState = (AuthorizationWaitingReactiveState) getPreviousState();
                    final ReactiveState innerState = authorizationWaitingState.getPreviousState();
                    final long innerStateId = innerState .getActionId();
                    return innerStateId == successEvent.getActionId() ?
                            CancellationReactiveState.newInstance(this, getActionId()) : authorizationWaitingState;
                }
                return  CancellationReactiveState.newInstance(this, getActionId());
            case CANCEL:
                return this;
            default:
                return super.onEvent(event);
        }
    }

}
