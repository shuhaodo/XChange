package org.knowm.xchange.binance.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BinanceMarginCurrency {
    private String coin;
    private boolean borrowable;
    private BigDecimal interest;

    public boolean isBorrowable() {
        return borrowable;
    }

    public BinanceMarginCurrency(
            @JsonProperty("coin") String coin,
            @JsonProperty("borrowable") boolean borrowable,
            @JsonProperty("dailyInterest") BigDecimal interest
    ) {
        this.coin = coin;
        this.borrowable = borrowable;
        this.interest = interest;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public String getCoin() {
        return coin;
    }
}
