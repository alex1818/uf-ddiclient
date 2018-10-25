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

import com.kynetics.updatefactory.ddiclient.api.model.response.DdiArtifact;
import com.kynetics.updatefactory.ddiclient.api.model.response.DdiChunk;
import com.kynetics.updatefactory.ddiclient.core.model.Distribution;
import com.kynetics.updatefactory.ddiclient.core.model.FileInfo;
import com.kynetics.updatefactory.ddiclient.core.model.Hash;
import com.kynetics.updatefactory.ddiclient.core.model.SoftwareModule;
import com.kynetics.updatefactory.ddiclient.core.model.event.AbstractEvent;
import com.kynetics.updatefactory.ddiclient.core.model.event.DownloadRequestEvent;

import java.util.ArrayList;
import java.util.List;

import static com.kynetics.updatefactory.ddiclient.api.model.response.DdiDeployment.HandlingType.FORCED;
import static com.kynetics.updatefactory.ddiclient.core.model.state.State.StateName.UPDATE_INITIALIZATION;
import static com.kynetics.updatefactory.ddiclient.core.model.state.StateImpl.StateBuilder;

/**
 * @author Daniele Sergio
 */
public class UpdateInitialization extends com.kynetics.updatefactory.ddiclient.core.model.state.AbstractReactiveState {

    private UpdateInitialization(State state) {
        super(state);
    }

    public static UpdateInitialization newInstance(ReactiveState previousState, long actionId) {
        StateImpl.StateBuilder stateBuilder = StateBuilder.builder(previousState)
                .withStateName(UPDATE_INITIALIZATION)
                .withPreviousState(null)
                .withActionId(actionId);
        return new UpdateInitialization(stateBuilder.build());
    }

    @Override
    public ReactiveState onEvent(AbstractEvent event) {
        switch (event.getEventName()) {
            case DOWNLOAD_REQUEST:
                final DownloadRequestEvent downloadRequestEvent = ((DownloadRequestEvent) event);
                final List<SoftwareModule> softwareModuleList = new ArrayList<>(downloadRequestEvent.getDdiDeploymentBase().getDeployment().getChunks().size());
                int numberOfArtifacts = 0;
                for (DdiChunk chunk : downloadRequestEvent.getDdiDeploymentBase().getDeployment().getChunks()) {
                    final List<FileInfo> fileInfoList = new ArrayList<>(chunk.getArtifacts().size());
                    for (DdiArtifact artifact : chunk.getArtifacts()) {
                        fileInfoList.add(new FileInfo(
                                artifact.getLink("download-http").parseLink2(),
                                new Hash(artifact.getHashes().getMd5(),
                                        artifact.getHashes().getSha1()),
                                artifact.getSize()));
                    }
                    softwareModuleList.add(new SoftwareModule(chunk.getPart().equals("bApp") ? SoftwareModule.Type.APP : SoftwareModule.Type.OS, fileInfoList));
                    numberOfArtifacts += fileInfoList.size();
                }
                Distribution distribution = new Distribution(softwareModuleList,false);
                final boolean isForced = downloadRequestEvent.getDdiDeploymentBase().getDeployment().getDownload() == FORCED;
                final boolean noFile = numberOfArtifacts == 0;
                final AbstractReactiveState state = noFile ?
                        UpdateEndedReactiveState.newInstance(this, getActionId(), new StateImpl.UpdateResponseImpl(true, new String[]{"Update doesn't have file"})):
                        UpdateDownloadReactiveState.newInstance(this, getActionId(), isForced, distribution, null);
                return isForced || noFile ? state :
                        AuthorizationWaitingReactiveState.newInstance(state, getActionId());

            default:
                return super.onEvent(event);
        }
    }
}