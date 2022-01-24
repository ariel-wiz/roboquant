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

package org.roboquant.binance

import com.binance.api.client.BinanceApiRestClient
import org.roboquant.common.Logging
import org.roboquant.common.Timeframe
import org.roboquant.feeds.HistoricPriceFeed
import org.roboquant.feeds.PriceBar
import java.time.Instant

/**
 * Create a new feed based on price actions coming from the Binance exchange.
 *
 * @property useMachineTime
 * @constructor
 *
 */
class BinanceHistoricFeed(apiKey: String? = null, secret: String? = null, private val useMachineTime: Boolean = true) :
    HistoricPriceFeed() {

    private var client: BinanceApiRestClient
    private val logger = Logging.getLogger(BinanceHistoricFeed::class)
    private val factory = BinanceConnection.getFactory(apiKey, secret)

    val availableAssets by lazy {
        BinanceConnection.retrieveAssets(factory)
    }

    init {
        client = factory.newRestClient()
    }

    /**
     * Retrieve [PriceBar] data for the provides [symbols]. It will retrieve the data for the provided [timeframe],
     * given that it doesn't exceed the limits enforced by Binance.
     */
    fun retrieve(
        vararg symbols: String,
        timeframe: Timeframe,
        interval: Interval = Interval.DAILY,
        limit: Int = 1000
    ) {
        require(symbols.isNotEmpty()) { "You need to provide at least 1 symbol" }
        val startTime = timeframe.start.toEpochMilli()
        val endTime = timeframe.end.toEpochMilli()
        for (symbol in symbols) {
            val asset = availableAssets[symbol]
            if (asset != null ) {
                val bars = client.getCandlestickBars(asset.symbol, interval, limit, startTime, endTime)
                for (bar in bars) {
                    val action = PriceBar(
                        asset,
                        bar.open.toDouble(),
                        bar.high.toDouble(),
                        bar.low.toDouble(),
                        bar.close.toDouble(),
                        bar.volume.toDouble()
                    )
                    val now = Instant.ofEpochMilli(bar.closeTime)
                    add(now, action)
                }
                logger.fine { "Retrieved $asset for $timeframe" }
            } else {
                logger.warning{ "$symbol not found" }
            }

        }
    }


}

