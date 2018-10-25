/*
 * Copyright © 2017-2018 Kynetics LLC
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 */

package com.kynetics.updatefactory.ddiclient.core.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Daniele Sergio
 */
public class SoftwareModule {
    public enum Type{
        OS, APP
    }

    private final Type type;

    private final Queue<FileInfo> fileInfoQueue = new LinkedList<>();
    private final FileInfo currentFileInfo;

    private final long id;

    public SoftwareModule(Type type, Collection<FileInfo> fileInfoList) {
        this.type = type;
        this.fileInfoQueue.addAll(fileInfoList);
        this.currentFileInfo = fileInfoQueue.poll();
        id = currentFileInfo != null ? currentFileInfo.getLinkInfo().getSoftwareModules() : -1;
    }

    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public FileInfo getCurrentFile() {
        return currentFileInfo;
    }

    public SoftwareModule nextStep(){
        return new SoftwareModule(type, fileInfoQueue);
    }

    public boolean currentFileIsLast(){
        return fileInfoQueue.isEmpty();
    }
}
