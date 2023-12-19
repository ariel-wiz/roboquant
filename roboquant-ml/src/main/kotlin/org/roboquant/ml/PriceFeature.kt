/*
 * Copyright 2020-2023 Neural Layer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.roboquant.ml

import org.roboquant.common.Asset
import org.roboquant.feeds.Event

/**
 * Extract the price from the event for the provided [asset]
 *
 * @param asset the asset to use
 * @param type the type of price to use
 * @param name the name of the feature
 */
class PriceFeature(
    private val asset: Asset,
    private val type: String = "DEFAULT",
    override val name: String = "${asset.symbol}-PRICE-$type"
) : SingleValueFeature() {

    /**
     * @see SingleValueFeature.calculateValue
     */
    override fun calculateValue(event: Event): Double {
        val action = event.prices[asset]
        return action?.getPrice(type) ?: Double.NaN
    }


}
