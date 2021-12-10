package org.knowm.xchange.binance.dto.trade;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderSideEffect {
    NO_SIDE_EFFECT,
    MARGIN_BUY,
    AUTO_REPAY;

    @JsonCreator
    public static OrderSideEffect getOrderSideEffect(String s) {
        try {
            return OrderSideEffect.valueOf(s);
        } catch (Exception e) {
            throw new RuntimeException("Unknown order side effect " + s + ".");
        }
    }
}
