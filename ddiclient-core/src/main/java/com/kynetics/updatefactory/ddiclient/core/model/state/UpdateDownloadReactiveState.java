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
import com.kynetics.updatefactory.ddiclient.core.model.event.DownloadStartedEvent;

import java.util.List;

import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.UPDATE_DOWNLOAD;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class UpdateDownloadReactiveState extends AbstractReactiveState {
    public static UpdateDownloadReactiveState newInstance(ReactiveState previousState,
                                                          long actionId,
                                                          boolean isForced,
                                                          Distribution distribution,
                                                          Hash lastHash) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(UPDATE_DOWNLOAD)
                .withPreviousState(null)
                .withIsForced(isForced)
                .withDistribution(distribution)
                .withLastHash(lastHash)
                .withActionId(actionId);
        return new UpdateDownloadReactiveState(stateBuilder.build());
    }

    private UpdateDownloadReactiveState(State state) {
        super(state);
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case DOWNLOAD_STARTED:
                return SavingFileReactiveState.newInstance(this, getActionId(), isForced(), getDistribution(), getLastHash(), ((DownloadStartedEvent) event).getInputStream());
            // TODO: 10/25/18 before refactoring return new SavingFileReactiveState(getActionId(), isUpdateForced(), getFileInfoList(), getNextFileToDownload(), getLastHash(), ((DownloadStartedEvent) event).getInputStream());
            default:
                return super.onEvent(event);
        }
    }
}
