package org.knowm.xchange.binance.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.dto.meta.CurrencyMetaData;

import java.math.BigDecimal;

public class BinanceAsset {
    private BigDecimal withdrawFee;
    private BigDecimal minWithdrawAmount;
    private boolean withdrawStatus;
    private boolean depositStatus;

    public BinanceAsset(
            @JsonProperty("withdrawFee") BigDecimal withdrawFee,
            @JsonProperty("minWithdrawAmount") BigDecimal minWithdrawAmount,
            @JsonProperty("withdrawStatus") boolean withdrawStatus,
            @JsonProperty("depositStatus") boolean depositStatus
    ) {
        this.withdrawFee = withdrawFee;
        this.minWithdrawAmount = minWithdrawAmount;
        this.withdrawStatus = withdrawStatus;
        this.depositStatus = depositStatus;
    }

    public CurrencyMetaData toCurrencyMeta() {
        return new CurrencyMetaData(withdrawFee, minWithdrawAmount, withdrawStatus, depositStatus);
    }
}
