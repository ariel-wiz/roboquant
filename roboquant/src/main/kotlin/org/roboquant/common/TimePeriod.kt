/*
 * Copyright 2020-2023 Neural Layer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package org.roboquant.common

import java.time.*

private fun createDuration(hours: Int, minutes: Int, seconds: Int, nanos: Long): Duration {
    var result = Duration.ZERO
    if (hours != 0) result = result.plusHours(hours.toLong())
    if (minutes != 0) result = result.plusMinutes(minutes.toLong())
    if (seconds != 0) result = result.plusSeconds(seconds.toLong())
    if (nanos != 0L) result = result.plusNanos(nanos)
    return result
}


@Deprecated("Renamed to TimePeriod", ReplaceWith("TimePeriod","org.roboquant.common.TimePeriod"))
typealias TradingPeriod=TimePeriod

/**
 * TimePeriod is an immutable class that unifies the JVM classes Duration and Period and allows to use periods
 * more easily in your code. It can store periods as small as nanosecond.
 */
class TimePeriod internal constructor(internal val period: Period, internal val duration: Duration) {

    /**
     * Create a new instance of TimePeriod
     */
    constructor(
        years: Int = 0,
        months: Int = 0,
        days: Int = 0,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0,
        nanos: Long = 0L
    ): this(Period.of(years, months, days), createDuration(hours, minutes, seconds, nanos))

    /**
     * Add an [other] trading period
     */
    operator fun plus(other: TimePeriod) =
        TimePeriod(period.plus(other.period), duration.plus(other.duration))

    /**
     * Subtract an [other] trading period
     */
    operator fun minus(other: TimePeriod) =
        TimePeriod(period.minus(other.period), duration.minus(other.duration))

    /**
     * String representation
     */
    override fun toString(): String {
        return "$period $duration"
    }

    /**
     * Only equals if all values are the same
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other is TimePeriod) {
            period == other.period && duration == other.duration
        } else {
            false
        }
    }

    /**
     * @see [Object.hashCode]
     */
    override fun hashCode(): Int {
        return period.hashCode() + duration.hashCode()
    }

}


/*********************************************************************************************
 * Extensions on Int type to make instantiation of TradingPeriods convenient
 *********************************************************************************************/

/**
 * Convert an Int to a [TimePeriod] of years
 */
val Int.years
    get() = TimePeriod(this)

/**
 * Convert an Int to a [TimePeriod] of months
 */
val Int.months
    get() = TimePeriod(0,this)

/**
 * Convert an Int to a [TimePeriod] of days
 */
val Int.days
    get() = TimePeriod(0,0,this)

/**
 * Convert an Int to a [TimePeriod] of hours
 */
val Int.hours
    get() = TimePeriod(0,0,0, this)

/**
 * Convert an Int to a [TimePeriod] of minutes
 */
val Int.minutes
    get() = TimePeriod(0,0,0,0,this)

/**
 * Convert an Int to a [TimePeriod] of seconds
 */
val Int.seconds
    get() = TimePeriod(0,0,0,0,0,this,0L)

/**
 * Convert an Int to a [TimePeriod] of milliseconds
 */
val Int.millis
    get() = (this * 1_000_000L).nanos

/**
 * Convert an Int to a [TimePeriod] of nanoseconds
 */
val Long.nanos
    get() = TimePeriod(0,0,0,0,0,0,this)

/**
 * Add a [period] using the provided [zoneId]
 */
fun Instant.plus(period: TimePeriod, zoneId: ZoneId): Instant {
    // Optimized path for HFT
    val result = if (period.period == Period.ZERO) this else atZone(zoneId).plus(period.period).toInstant()
    return result.plus(period.duration)
}

/**
 * Subtract a [period] using the provided [zoneId]
 */
fun Instant.minus(period: TimePeriod, zoneId: ZoneId): Instant {
    // Optimized path for HFT
    val result = if (period.period == Period.ZERO) this else atZone(zoneId).minus(period.period).toInstant()
    return result.minus(period.duration)
}

/**
 * Add a [period] using UTC
 */
operator fun Instant.plus(period: TimePeriod) = plus(period, ZoneOffset.UTC)

/**
 * Add a [period]
 */
operator fun ZonedDateTime.plus(period: TimePeriod): ZonedDateTime = plus(period.period).plus(period.duration)

/**
 * Subtract a [period] using UTC
 */
operator fun Instant.minus(period: TimePeriod) = minus(period, ZoneOffset.UTC)

/**
 * Subtract a [period]
 */
operator fun ZonedDateTime.minus(period: TimePeriod): ZonedDateTime = minus(period.period).minus(period.duration)
