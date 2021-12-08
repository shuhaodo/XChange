package org.knowm.xchange.binance.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

import java.math.BigDecimal;

public class BinanceMarginPair {
    public String id;
    private String symbol;
    private String base;
    private String quote;
    private boolean isMarginTrade;
    private boolean isBuyAllowed;
    private boolean isSellAllowed;

    private CurrencyPair pair;

    public boolean isEnabled() {
        return isMarginTrade && isBuyAllowed && isSellAllowed;
    }

    public BinanceMarginPair(
            @JsonProperty("id") String id,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("base") String base,
            @JsonProperty("quote") String quote,
            @JsonProperty("isMarginTrade") boolean isMarginTrade,
            @JsonProperty("isBuyAllowed") boolean isBuyAllowed,
            @JsonProperty("isSellAllowed") boolean isSellAllowed
    ) {
        this.id = id;
        this.symbol = symbol;
        this.base = base;
        this.quote = quote;
        this.isMarginTrade = isMarginTrade;
        this.isBuyAllowed = isBuyAllowed;
        this.isSellAllowed = isSellAllowed;
    }

    public synchronized CurrencyPair toPair() {
        CurrencyPair currencyPair = pair;
        if (currencyPair == null) {
            currencyPair = BinanceAdapters.adaptSymbol(symbol);
        }
        return currencyPair;
    }
}
