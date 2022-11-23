/*
 * Copyright 2020-2022 Neural Layer
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

package org.roboquant.strategies.utils

import org.roboquant.common.PriceSeries
import kotlin.test.*

internal class PriceSeriesTest {

    @Test
    fun test() {
        val buffer = PriceSeries(10)
        repeat(5) { buffer.add(1.0) }
        assertFalse(buffer.isFilled())

        repeat(10) { buffer.add(1.0) }
        assertTrue(buffer.isFilled())

        val d = buffer.toDoubleArray()
        assertEquals(10, d.size)
    }

}