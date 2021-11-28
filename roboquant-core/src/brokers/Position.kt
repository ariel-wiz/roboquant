/*
 * Copyright 2021 Neural Layer
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

package org.roboquant.brokers

import org.roboquant.common.Asset
import java.time.Instant
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Position of an asset in the portfolio. This implementation makes no assumptions about the asset class, so it supports
 * any type of asset class from stocks and options to crypto currencies.
 *
 * TODO: possible move to an read-only data structure for Position
 *
 * @property asset the asset
 * @property quantity volume of the asset, not including any contract multiplier defined by the asset
 * @property cost average cost for an asset, in the currency denoted by the asset
 * @property price Last known market price for this asset
 * @property lastUpdate When was the market price last updated
 * @constructor Create a new Position
 */
data class Position(
    val asset: Asset,
    var quantity: Double,
    var cost: Double = 0.0,
    var price: Double = cost,
    var lastUpdate: Instant = Instant.MIN
) {

    /**
     * Total size of a position is the position quantity times the asset multiplier. For many asset classes the
     * multiplier will be 1, but for example for option contracts it will often be 100
     */
    val totalSize: Double
        get() = quantity * asset.multiplier

    companion object Factory {
        /**
         * Create an empty position for the provided [asset] and return this.
         */
        fun empty(asset: Asset): Position = Position(asset, 0.0, 0.0, 0.0)
    }


    /**
     * Update the current position with a new [position] and return the P&L realized by this update.
     */
    fun update(position: Position): Double {
        val newQuantity = quantity + position.quantity
        var pnl = 0.0

        // Check if we need to update the average price and pnl
        when {
            quantity.sign != newQuantity.sign -> {
                pnl = totalSize * (position.cost - cost)
                cost = position.cost
            }

            newQuantity.absoluteValue > quantity.absoluteValue -> {
                cost = (cost * quantity + position.cost * position.quantity) / newQuantity
            }

            else -> {
                pnl = position.quantity * asset.multiplier * (cost - position.cost)
            }
        }

        quantity = newQuantity
        return pnl
    }

    /**
     * Is this a short position
     */
    val short: Boolean
        get() = quantity < 0

    /**
     * is this a long position
     */
    val long: Boolean
        get() = quantity > 0

    /**
     * Is this an open position
     */
    val open: Boolean
        get() = quantity != 0.0

    /**
     * The unrealized profit & loss for this position based on the [cost] and last known market [price],
     * in the currency denoted by the asset
     */
    val pnl: Double
        get() = totalSize * (price - cost)


    /**
     * The total value for this position based on last known market price, in the currency denoted by the asset.
     * Short positions will return a negative value.
     */
    val value: Double
        get() = totalSize * price

    /**
     * The gross exposure for this position based on last known market price, in the currency denoted by the asset.
     * The difference with the [value] property is that short positions also result in a positive exposure.
     *
     */
    val exposure: Double
        get() = totalSize.absoluteValue * price


    /**
     * The total cost of this position, in the currency denoted by the asset. Short positions will typically have a
     * negative cost.
     *
     * @return
     */
    val totalCost: Double
        get() = totalSize * cost


}

