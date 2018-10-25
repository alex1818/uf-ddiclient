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
import com.kynetics.updatefactory.ddiclient.core.model.event.AbstractEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.CancelEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.FileCorruptedEvent;

import java.io.InputStream;
import java.util.List;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.SAVING_FILE;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class SavingFileReactiveState extends AbstractReactiveState {

    public static SavingFileReactiveState newInstance(ReactiveState previousState,
                                                          long actionId,
                                                          boolean isForced,
                                                          Distribution distribution,
                                                          Hash lastHash,
                                                      InputStream inputStream) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(SAVING_FILE)
                .withPreviousState(null)
                .withIsForced(isForced)
                .withDistribution(distribution)
                .withLastHash(lastHash)
                .withActionId(actionId)
                .withInputStream(inputStream); //todo add download notifier
        return new SavingFileReactiveState(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case SUCCESS:
                final Distribution nextDistribution = getDistribution().nextStep(true);
                return nextDistribution == null ?
                        UpdateReadyReactiveState.newInstance(this, getActionId(), isForced()) :
                        UpdateDownloadReactiveState.newInstance(this, getActionId(), isForced(), nextDistribution, null);
            case CANCEL:
                return CancellationCheckReactiveState.newInstance(this, ((CancelEvent) event).getActionId());
            case FILE_CORRUPTED:
                final FileCorruptedEvent corruptedEvent = (FileCorruptedEvent) event;
                final Hash currentHash = corruptedEvent.getDownloadedFileHash();
                return currentHash == null || getLastHash() == null || !currentHash.equals(getLastHash()) ?
                        UpdateDownloadReactiveState.newInstance(this, getActionId(), isForced(), getDistribution(), currentHash) :
                        ServerFileCorruptedReactiveState.newInstance(this, getActionId());
            case DOWNLOAD_PENDING:
                return this;
            default:
                return super.onEvent(event);
        }
    }

    private SavingFileReactiveState(State state) {
        super(state);
    }
}
