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

package org.roboquant.ta

import org.junit.jupiter.api.Test
import org.roboquant.common.Asset
import org.roboquant.feeds.PriceBar
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PriceBarSeriesTest {

    private val asset = Asset("DEMO")
    private val pb = PriceBar(asset, 10, 11, 9, 10, 100)

    @Test
    fun test() {
        val pbs = PriceBarSeries(10)
        repeat(5) { pbs.add(pb) }
        assertFalse(pbs.isFilled())
        assertEquals(5, pbs.size)

        repeat(5) { pbs.add(pb) }
        assertTrue(pbs.isFilled())
        assertEquals(10, pbs.size)

        repeat(5) { pbs.add(pb) }
        assertTrue(pbs.isFilled())
        assertEquals(10, pbs.size)

        assertEquals(10, pbs.open.size)
        assertEquals(10, pbs.typical.size)

        pbs.clear()
        assertFalse(pbs.isFilled())
        assertEquals(10, pbs.open.size)
        assertTrue { pbs.close.all { it.isNaN() } }
    }


}