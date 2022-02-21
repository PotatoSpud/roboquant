package org.roboquant.orders



abstract class CombinedOrder(vararg orders: SingleOrder, id: String) : Order(orders.first().asset, id) {

    init {
        require(orders.map { it.asset }.distinct().size == 1) { "Combined Orders can only contain orders for the same asset"}
    }

}

class OneCancelsOtherOrder(
    val first: SingleOrder,
    val second: SingleOrder,
    id: String = nextId(),
) : CombinedOrder(first, second, id = id)


class OneTriggersOtherOrder(
    val first: SingleOrder,
    val second: SingleOrder,
    id: String = nextId(),
) : CombinedOrder(first, second, id = id)



class BracketOrder(
    val entry: SingleOrder,
    val takeProfit: SingleOrder,
    val stopLoss: SingleOrder,
    id: String = nextId(),
) : CombinedOrder(entry, takeProfit, stopLoss, id = id) {

    init {
        require(entry.quantity == -takeProfit.quantity && entry.quantity == -stopLoss.quantity)
    }

}
