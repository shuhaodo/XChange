package org.knowm.xchange.binance.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BinanceMarginAccountInformation {
    @JsonProperty("tradeEnabled")
    private boolean tradeEnabled;
    @JsonProperty("borrowEnabled")
    private boolean borrowEnabled;
    @JsonProperty("userAssets")
    private List<MarginAsset> userAssets;
}
