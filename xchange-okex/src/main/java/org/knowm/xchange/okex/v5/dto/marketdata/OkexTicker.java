package org.knowm.xchange.okex.v5.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class OkexTicker {
    @JsonProperty("instId")
    private String instId;

    @JsonProperty("last")
    private BigDecimal last;

    @JsonProperty("lastSz")
    private BigDecimal lastSz;

    @JsonProperty("askPx")
    private BigDecimal askPx;

    @JsonProperty("askSz")
    private BigDecimal askSz;

    @JsonProperty("bidPx")
    private BigDecimal bidPx;

    @JsonProperty("bidSz")
    private BigDecimal bidSz;

    @JsonProperty("open24h")
    private BigDecimal open24h;

    @JsonProperty("high24h")
    private BigDecimal high24h;

    @JsonProperty("low24h")
    private BigDecimal low24h;

    @JsonProperty("volCcy24h")
    private BigDecimal volCcy24h;

    @JsonProperty("vol24h")
    private BigDecimal vol24h;

    @JsonProperty("ts")
    private Long ts;
}
