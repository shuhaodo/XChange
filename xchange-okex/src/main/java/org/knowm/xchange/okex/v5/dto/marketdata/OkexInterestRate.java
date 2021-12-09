package org.knowm.xchange.okex.v5.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class OkexInterestRate {
    @JsonProperty("ccy")
    private String currency;

    @JsonProperty("quota")
    private String quota;

    @JsonProperty("rate")
    private BigDecimal rate;
}
