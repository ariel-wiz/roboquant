package org.roboquant.orders

/**
 * The status an order can be in. The flow is straight forward and is adhered to by all broker implementations, even
 * brokers that have more order statuses.
 *
 *  - [INITIAL] -> [ACCEPTED] -> [COMPLETED] | [CANCELLED] | [EXPIRED]
 *  - [INITIAL] -> [REJECTED]
 *
 *  At any given time an [OrderState] is either [open] or [closed] state. Once an order reaches a [closed] state,
 *  it cannot be opened again and will not be further processed.
 */
enum class OrderStatus {

    /**
     * State of an order that has just been created. It will remain in this state until it is either
     * [REJECTED] or [ACCEPTED] by the broker.
     */
    INITIAL,

    /**
     * The order has been received, validated and accepted by the broker. The order remains in this state until it
     * goes to one of the end-states.
     */
    ACCEPTED,

    /**
     * The order has been successfully completed. This is an end-state.
     */
    COMPLETED,

    /**
     * The order was cancelled, normally by a [CancelOrder]. This is an end-state.
     */
    CANCELLED,

    /**
     *  The order has expired, normally triggered by a [TimeInForce] policy. This is an end-state.
     */
    EXPIRED,

    /**
     *  The order has been rejected. This is an end-state and could occur when:
     *  - The order is not valid, for example you try to short an asset while that is not allowed
     *  - You don't have enough buyingPower to place a new order
     *  - The order type is not supported by the broker
     *  - The provided asset is not recognised or cannot be traded on your account
     */
    REJECTED;

    /**
     * Returns true if the order has been aborted. That implies it is in one of the following three "error" end-states:
     * [CANCELLED], [EXPIRED], [REJECTED]
     */
    val aborted: Boolean
        get() = this === CANCELLED || this === EXPIRED || this === REJECTED

    /**
     * Returns true if the order closed. This means it has reached an end-state that doesn't allow for any more trading.
     * This implies it is in one of these four possible end-states: [COMPLETED], [CANCELLED], [EXPIRED] or [REJECTED].
     */
    val closed: Boolean
        get() = this === COMPLETED || this === CANCELLED || this === EXPIRED || this === REJECTED

    /**
     * Returns true if the order in an open state, so either [INITIAL] or [ACCEPTED]
     */
    val open: Boolean
        get() = this === INITIAL || this === ACCEPTED

}