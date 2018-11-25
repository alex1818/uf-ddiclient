/*
 * Copyright © 2017-2018 Kynetics LLC
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 */

package com.kynetics.updatefactory.ddiclient.core.servicecallback;

import com.kynetics.updatefactory.ddiclient.core.model.FileInfo;

import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * @author Daniele Sergio
 */
public interface SystemOperation {
    boolean savingFile(InputStream inputStream, FileInfo fileInfo);

    Future<UpdateStatus> executeUpdate(long actionId);

    enum UpdateStatus {
        APPLIED_WITH_ERROR, SUCCESSFULLY_APPLIED
    }
}
