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

package org.roboquant.orders

/**
 * Various state that an order can be in. The  flow is straight forward:
 *
 *  - [INITIAL] -> [ACCEPTED] -> [COMPLETED] | [CANCELLED] | [EXPIRED]
 *  - [INITIAL] -> [REJECTED]
 *
 *  At any given time an order is either [open] or [closed]
 */
enum class OrderStatus {

    /**
     * State of an order that has just been created. It will remain in this state until it is either
     * rejected or accepted.
     */
    INITIAL,

    /**
     * The order has been received, validated and accepted.
     */
    ACCEPTED,

    /**
     * The order has been successfully completed. This is an end state
     */
    COMPLETED,

    /**
     * The order was cancelled, normally by a cancellation order. This is an end state
     */
    CANCELLED,

    /**
     *  The order has expired, normally by a time-in-force policy. This is an end state
     */
    EXPIRED,

    /**
     *  The order has been rejected. This is an end state
     */
    REJECTED;

    /**
     * Has the order been aborted. That implies it is in one of the following three "error" end states:
     * [CANCELLED], [EXPIRED], [REJECTED]
     */
    val aborted: Boolean
        get() = this === CANCELLED || this === EXPIRED || this === REJECTED

    /**
     * Is the order closed. This means it has reached an end-state that doesn't allow for any more trading. This implies it
     * is in one of these four possible end-states: [COMPLETED], [CANCELLED], [EXPIRED] or [REJECTED].
     */
    val closed: Boolean
        get() = this === COMPLETED || this === CANCELLED || this === EXPIRED || this === REJECTED


    /**
     * Is the order in an open state, so [INITIAL] or [ACCEPTED]
     */
    val open: Boolean
        get() = !closed


}
