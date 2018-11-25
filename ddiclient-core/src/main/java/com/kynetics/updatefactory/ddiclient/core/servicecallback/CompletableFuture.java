/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.core.servicecallback;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Daniele Sergio
 */
public
class CompletableFuture<T> implements Future<T> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private T value;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public T get() throws InterruptedException {
        latch.await();
        return value;
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return value;
        } else {
            throw new TimeoutException();
        }
    }

    public void put(T result) {
        value = result;
        latch.countDown();
    }
}
