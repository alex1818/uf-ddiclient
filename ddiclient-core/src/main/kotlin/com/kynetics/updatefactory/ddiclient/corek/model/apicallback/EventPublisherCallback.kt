/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model.apicallback

import com.kynetics.updatefactory.ddiclient.corek.model.EventPublisher

/**
 * @author Daniele Sergio
 */
abstract class EventPublisherCallback<T>(protected val eventPublisher: EventPublisher): LogCallback<T>()