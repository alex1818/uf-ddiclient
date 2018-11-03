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

typealias ReducerType<S, A> = (S, A) -> S

typealias StoreCreatorType<S, A, R> = (ReducerType<S, A>, S) -> Store<S, A, R>

typealias DispatcherType<A, R> = (A) -> R

typealias Subscription<S> = (S, S) -> Unit

typealias UnSubscription = () -> Unit

typealias EnhancerType<S, A1, R1, A2, R2> = (StoreCreatorType<S, A1, R1>) -> StoreCreatorType<S, A2, R2>

typealias MiddlewareType<S, A1, R1, A2, R2> = (MiddlewareApi<S, A1, R1>) -> (DispatcherType<A1, R1>) -> DispatcherType<A2, R2>