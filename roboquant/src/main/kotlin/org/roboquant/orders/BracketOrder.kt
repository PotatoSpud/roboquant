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

package org.roboquant.orders

/**
 * Bracket order that enables you to place an order and at the same time place orders to take profit and restrict loss.
 *
 * Although the SimBroker is very flexible and support any type of single order, real brokers often are more limited.
 * So it is advised to restrict your orders to the following subsets if you want to go live:
 *
 * - entry order is either a Market or Limit order
 * - takeProfit is a Limit order
 * - stopLoss is a StopLoss or StopLimit order
 *
 * @property entry the entry order
 * @property takeProfit the take profit order
 * @property stopLoss the stop loss order
 * @param tag and optional tag, default is an empty string
 * @constructor create a new instance of a BracketOrder
 */
class BracketOrder(
    val entry: SingleOrder,
    val takeProfit: SingleOrder,
    val stopLoss: SingleOrder,
    tag: String = ""
) : CreateOrder(entry.asset, tag) {

    init {
        require(entry.asset == takeProfit.asset && entry.asset == stopLoss.asset) {
            "bracket orders can only contain orders for the same asset"
        }
        require(entry.size == -takeProfit.size && entry.size == -stopLoss.size) {
            "bracket orders takeProfit and stopLoss orders need to close the entry order"
        }
    }

    override fun info() = sortedMapOf("entry" to entry, "takeProfit" to takeProfit, "stopLoss" to "stopLoss")
}