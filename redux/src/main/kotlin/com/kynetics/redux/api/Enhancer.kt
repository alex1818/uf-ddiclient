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
interface Enhancer<S: State<*>, A1: Action<*>, R1, A2: Action<*>, R2>{
    fun apply(storeCreator: StoreCreatorType<S, A1, R1>): StoreCreatorType<S, A2, R2>
}