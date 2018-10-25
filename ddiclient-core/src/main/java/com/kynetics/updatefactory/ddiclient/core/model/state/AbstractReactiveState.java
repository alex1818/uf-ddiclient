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

import com.kynetics.updatefactory.ddiclient.core.model.Distribution;
import com.kynetics.updatefactory.ddiclient.core.model.Hash;
import com.kynetics.updatefactory.ddiclient.core.model.event.AbstractEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.ErrorEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.FailureEvent;

import java.io.InputStream;

import static com.kynetics.updatefactory.ddiclient.core.model.state.AbstractCommunicationReactiveState.MAX_ATTEMPTS;


/**
 * @author Daniele Sergio
 */
public abstract class AbstractReactiveState implements ReactiveState{

    private final State state;

    public AbstractReactiveState(State state) {
        this.state = state;
    }

    public ReactiveState onEvent(AbstractEvent event){
        switch (event.getEventName()){
            case ERROR:
                ErrorEvent errorEvent = (ErrorEvent) event;
                return getStateOnError(errorEvent, this, MAX_ATTEMPTS);
            case FAILURE:
                FailureEvent failureEvent = (FailureEvent) event;
                return CommunicationFailureReactiveState.newInstance(this,new StateImpl.ErrorImpl(-1, new String[0], failureEvent.getThrowable()), MAX_ATTEMPTS);
            case FORCE_CANCEL:
                return WaitingReactiveState.fromPreviousState(this, 0); // todo before refactor: new WaitingReactiveState(30_000,null);
            default:
                throw new IllegalStateException(String.format("AbstractEvent %s not handler in %s state", event.getEventName(), getStateName()));
        }
    }

    static AbstractReactiveState getStateOnError(ErrorEvent errorEvent, ReactiveState state, int retry) {
        return errorEvent.getCode() == 404 && errorEvent.getDetails()[0] != null &&
                errorEvent.getDetails()[0].equals("hawkbit.server.error.repo.entitiyNotFound") ?
                WaitingReactiveState.fromPreviousState(state, 0) : // todo before refactor: new WaitingReactiveState(0,null)
                CommunicationErrorReactiveState.newInstance(state, new StateImpl.ErrorImpl(errorEvent.getCode(), errorEvent.getDetails(), null), retry);
    }


    @Override
    public long getSleepTime() {
        return state.getSleepTime();
    }

    @Override
    public StateName getStateName() {
        return state.getStateName();
    }

    @Override
    public long getActionId() {
        return state.getActionId();
    }

    @Override
    public ReactiveState getPreviousState() {
        final State innerState = state.getPreviousState();
        if(innerState!=null && !(innerState instanceof ReactiveState)){
            throw new IllegalStateException(String.format("state must be instance of %s", ReactiveState.class.getCanonicalName()));
        }
        return (ReactiveState) innerState;
    }

    @Override
    public boolean isForced() {
        return state.isForced();
    }

    @Override
    public UpdateResponse getUpdateResponse() {
        return state.getUpdateResponse();
    }

    @Override
    public Hash getLastHash() {
        return state.getLastHash();
    }

    @Override
    public Error getError() {
        return state.getError();
    }

    @Override
    public InputStream getInputStream() {
        return state.getInputStream();
    }

    @Override
    public boolean isInputStreamAvailable() {
        return state.isInputStreamAvailable();
    }

    @Override
    public double getPercent() {
        return state.getPercent();
    }

    @Override
    public boolean hasInnerState() {
        return state.getPreviousState() != null;
    }

    @Override
    public Distribution getDistribution() {
        return state.getDistribution();
    }
}
