package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.knowm.xchange.okex.v5.dto.account.OkexWalletBalance;
import org.knowm.xchange.okex.v5.dto.trade.OkexOrderDetails;

import java.util.List;

@Getter
@NoArgsConstructor
public class OkExStreamTradeResponse {
    @JsonProperty("data")
    private List<OkexOrderDetails> data;

    @JsonProperty("arg")
    private OkExStreamArgInstrument arg;
}
