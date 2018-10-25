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
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Daniele Sergio
 */
public class Distribution {

    // TODO: 10/25/18 call primary constructor
    public Distribution(Collection<SoftwareModule> softwareModuleCollection, boolean error) {
        softwareModules.addAll(softwareModuleCollection);
        currentSoftwareModule = softwareModules.poll();
        this.error = error;
    }

    private Distribution(Collection<SoftwareModule> softwareModuleCollection, SoftwareModule softwareModule, boolean error) {
        softwareModules.addAll(softwareModuleCollection);
        currentSoftwareModule = softwareModule;
        this.error = error;
    }

    public SoftwareModule getCurrentSoftwareModule(){
        return currentSoftwareModule;
    }

    public Distribution nextStep(boolean currentSoftwareModuleSuccessfullyApplied){
        final boolean flag = !currentSoftwareModuleSuccessfullyApplied || error;
        if(!currentSoftwareModule.currentFileIsLast()){
            return new Distribution(softwareModules, currentSoftwareModule.nextStep(), flag);
        }

        if(softwareModules.isEmpty()){
            return null;
        }

        final Distribution nextDistribution = new Distribution(softwareModules, flag);

        return nextDistribution.getCurrentSoftwareModule() != null ? nextDistribution : nextDistribution.nextStep(flag);
    }


    public boolean hasNextSoftwareModule(){
        return softwareModules.isEmpty();
    }

    // TODO: 10/25/18 this is dinamic and depends by type 
    private static class SoftwareModuleComparatorByType implements Comparator<SoftwareModule>{
        @Override
        public int compare(SoftwareModule s1, SoftwareModule s2) {
            if(s1.getType() == s2.getType()){
                return 0;
            }
            return s1.getType() == SoftwareModule.Type.APP ? -1 : 1;
        }
    }

    private final Queue<SoftwareModule> softwareModules = new PriorityQueue<>(new SoftwareModuleComparatorByType());
    private final boolean error;
    private final SoftwareModule currentSoftwareModule;
}
