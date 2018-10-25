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
import com.kynetics.updatefactory.ddiclient.core.model.FileInfo;
import com.kynetics.updatefactory.ddiclient.core.model.Hash;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Daniele Sergio
 */
class StateImpl implements State {
    public static class ErrorImpl implements Error{

        @Override
        public long getCode() {
            return code;
        }

        @Override
        public String[] getDetails() {
            return details;
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

        public ErrorImpl(long code, String[] details, Throwable throwable) {
            this.code = code;
            this.details = details;
            this.throwable = throwable;
        }

        private final long code;
        private final String[] details;
        private final Throwable throwable;
    }


    public static class UpdateResponseImpl implements UpdateResponse {
        @Override
        public boolean isSuccessfullyUpdate() {
            return successfullyUpdate;
        }

        @Override
        public String[] getDetails() {
            return details;
        }

        public UpdateResponseImpl(boolean successfullyUpdate, String[] details) {
            this.successfullyUpdate = successfullyUpdate;
            this.details = details == null ? new String[0] : details;
        }

        private final boolean successfullyUpdate;
        private final String[] details;
    }

    static StateBuilder builder(){
        return new StateBuilder();
    }

    @Override
    public Distribution getDistribution() {
        return distribution;
    }

    @Override
    public long getSleepTime() {
        return sleepTime;
    }

    @Override
    public StateName getStateName() {
        return stateName;
    }

    @Override
    public long getActionId() {
        return actionId;
    }

    @Override
    public ReactiveState getPreviousState() {
        return reactiveState;
    }

    @Override
    public boolean isForced() {
        return isForced;
    }

    @Override
    public UpdateResponse getUpdateResponse() {
        return updateResponse;
    }

    @Override
    public Hash getLastHash() {
        return lastHash;
    }

    @Override
    public Error getError() {
        return error;
    }

    @Override
    public InputStream getInputStream() {
        final InputStream inputStream = inputStreamQueue.poll();
        if(inputStream == null){
            throw new IllegalStateException("inputStream not available");
        }
        return inputStream;
    }

    @Override
    public boolean isInputStreamAvailable() {
        return !inputStreamQueue.isEmpty();
    }

    @Override
    public double getPercent() {
        return percent;
    }

    @Override
    public boolean hasInnerState() {
        return reactiveState != null;
    }


    public StateImpl(long sleepTime, StateName stateName, ReactiveState reactiveState, long actionId, boolean isForced, Distribution distribution, UpdateResponse updateResponse, Hash lastHash, Error error, InputStream inputStream, double percent) {
        this.sleepTime = sleepTime;
        this.distribution = distribution;
        this.stateName = stateName;
        this.reactiveState = reactiveState;
        this.actionId = actionId;
        this.isForced = isForced;
        this.updateResponse = updateResponse;
        this.lastHash = lastHash;
        this.error = error;
        if(inputStream!=null) {
            this.inputStreamQueue.add(inputStream);
        }
        this.percent = percent;
    }

    static class StateBuilder {

        static StateBuilder builder(State state){
            final StateBuilder stateBuilder = new StateBuilder();
            stateBuilder.sleepTime = state.getSleepTime();
            stateBuilder.stateName = state.getStateName();
            stateBuilder.actionId = state.getActionId();
            stateBuilder.isForced = state.isForced();
            stateBuilder.updateResponse = state.getUpdateResponse();
            stateBuilder.lastHash = state.getLastHash();
            stateBuilder.error = state.getError();
            if(state.isInputStreamAvailable()){
                stateBuilder.inputStream = state.getInputStream();
            }
            stateBuilder.percent = state.getPercent();
            return stateBuilder;
        }

        static StateBuilder builder() {
            return new StateBuilder();
        }

        public StateBuilder withStateName(StateName stateName) {
            this.stateName = stateName;
            return this;
        }

        public StateBuilder withActionId(long actionId) {
            this.actionId = actionId;
            return this;
        }

        public StateBuilder withIsForced(boolean isForced) {
            this.isForced = isForced;
            return this;
        }

        public StateBuilder withUpdateStatus(UpdateResponse updateResponse) {
            this.updateResponse = updateResponse;
            return this;
        }

        public StateBuilder withLastHash(Hash lastHash) {
            this.lastHash = lastHash;
            return this;
        }

        public StateBuilder withError(Error error) {
            this.error = error;
            return this;
        }

        public StateBuilder withInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public StateBuilder withPercent(double percent) {
            this.percent = percent;
            return this;
        }

        public StateBuilder withSleepTime(long sleepTime) {
            this.sleepTime = sleepTime;
            return this;
        }
        public StateBuilder withPreviousState(ReactiveState reactiveState) {
            this.reactiveState = reactiveState;
            return this;
        }

        public StateBuilder withDistribution(Distribution distribution){
            this.distribution = distribution;
            return this;
        }



        public State build(){
            return new StateImpl(
                    sleepTime,
                    stateName,
                    reactiveState,
                    actionId,
                    isForced,
                    distribution,
                    updateResponse,
                    lastHash,
                    error,
                    inputStream,
                    percent);
        }

        StateBuilder() {
        }
        
        private StateName stateName;
        private ReactiveState reactiveState;
        private long actionId;
        private boolean isForced;
        private UpdateResponse updateResponse;
        private Hash lastHash;
        private Error error;
        private InputStream inputStream;
        private double percent;
        private long sleepTime;
        private Distribution distribution;

    }

    private final long sleepTime;
    private final Distribution distribution;
    private final StateName stateName;
    private ReactiveState reactiveState;
    private final long actionId;
    private final boolean isForced;
    private final UpdateResponse updateResponse;
    private final Hash lastHash;
    private final Error error;
    private final BlockingQueue<InputStream> inputStreamQueue = new ArrayBlockingQueue<>(1);
    private final double percent;

}
