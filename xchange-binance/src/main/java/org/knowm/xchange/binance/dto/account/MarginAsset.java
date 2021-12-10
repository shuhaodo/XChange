package org.knowm.xchange.binance.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.Currency;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class MarginAsset {
    @JsonProperty("asset")
    private String asset;
    @JsonProperty("free")
    private BigDecimal free;
    @JsonProperty("locked")
    private BigDecimal locked;
    @JsonProperty("borrowed")
    private BigDecimal borrowed;
    @JsonProperty("interest")
    private BigDecimal interest;
    @JsonProperty("netAsset")
    private BigDecimal netAsset;

    public Currency getCurrency() {
        return Currency.getInstance(asset);
    }
}
