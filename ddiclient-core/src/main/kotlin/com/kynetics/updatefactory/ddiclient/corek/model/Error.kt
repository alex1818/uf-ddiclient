/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.corek.model

import java.util.*

data class Error(
        val code: Int = -1,
        val details: Array<String> = arrayOf(""),
        val throwable: Throwable? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Error

        if (code != other.code) return false
        if (!Arrays.equals(details, other.details)) return false
        if (throwable != other.throwable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + Arrays.hashCode(details)
        result = 31 * result + (throwable?.hashCode() ?: 0)
        return result
    }
}