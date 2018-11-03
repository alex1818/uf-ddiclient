/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.redux.api

/**
 * @author Daniele Sergio
 */
interface Middleware<S: State<*>, A1: Action<*>, R1, A2: Action<*>, R2>{
    fun apply(middlewareApi: MiddlewareApi<S, A1, R1>):  (DispatcherType<A1, R1>) -> DispatcherType<A2, R2>
}

