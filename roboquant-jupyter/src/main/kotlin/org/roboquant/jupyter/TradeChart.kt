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

package org.roboquant.jupyter

import org.icepear.echarts.Option
import org.icepear.echarts.Scatter
import org.icepear.echarts.charts.scatter.ScatterSeries
import org.icepear.echarts.components.coord.cartesian.TimeAxis
import org.icepear.echarts.components.coord.cartesian.ValueAxis
import org.icepear.echarts.components.dataZoom.DataZoom
import org.icepear.echarts.components.tooltip.Tooltip
import org.roboquant.brokers.Trade
import org.roboquant.common.UnsupportedException
import java.math.BigDecimal
import java.time.Instant


internal fun Trade.getTooltip() : String {
    val pnl = pnl.toBigDecimal()
    val totalCost = totalCost.toBigDecimal()
    val fee = fee.toBigDecimal()
    return """
            |asset: ${asset.symbol}<br>
            |currency: ${asset.currency}<br>
            |time: $time<br>
            |size: $size<br>
            |fee: $fee<br>
            |pnl: $pnl<br>
            |cost: $totalCost<br>
            |order: $orderId""".trimMargin()
}

/**
 * Trade chart plots the trades of an [trades] that have been generated during a run. By default, the realized pnl of
 * the trades will be plotted but this can be changed. The possible options are pnl, fee, cost and quantity
 */
open class TradeChart(
    private val trades: List<Trade>,
    private val aspect: String = "pnl",
) : Chart() {

    init {
        val validAspects = listOf("pnl", "fee", "cost", "size")
        require(aspect in validAspects) { "Unsupported aspect $aspect, valid values are $validAspects" }
    }


    private fun tradesToSeriesData(): List<Triple<Instant, BigDecimal, String>> {
        val d = mutableListOf<Triple<Instant, BigDecimal, String>>()
        for (trade in trades.sortedBy { it.time }) {
            with(trade) {
                val value = when (aspect) {
                    "pnl" -> pnl.convert(time = time).toBigDecimal()
                    "fee" -> fee.convert(time = time).toBigDecimal()
                    "cost" -> totalCost.convert(time = time).toBigDecimal()
                    "size" -> size.toBigDecimal()
                    else -> throw UnsupportedException("Unsupported aspect $aspect")
                }

                val tooltip = trade.getTooltip()
                d.add(Triple(time, value, tooltip))
            }
        }
        return d
    }

    override fun getOption(): Option {

        val data = tradesToSeriesData()
        val max = data.maxOfOrNull { it.second }
        val min = data.minOfOrNull { it.second }

        val series = ScatterSeries()
            .setData(data)
            .setSymbolSize(10)

        val vm = getVisualMap(min, max).setDimension(1)

        val tooltip = Tooltip()
            .setFormatter(javascriptFunction("return p.value[2];"))

        val chart = Scatter()
            .setTitle(title ?: "Trade $aspect")
            .addXAxis(TimeAxis())
            .addYAxis(ValueAxis().setScale(true))
            .addSeries(series)
            .setVisualMap(vm)
            .setTooltip(tooltip)

        val option = chart.option
        option.setToolbox(getToolbox(includeMagicType = false))
        option.setDataZoom(DataZoom())

        return option
    }
}
