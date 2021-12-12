package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.knowm.xchange.okex.v5.dto.account.OkexWalletBalance;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexOrderbook;

import java.util.List;

@Getter
@NoArgsConstructor
public class OkExStreamAccountDataResponse {
    @JsonProperty("data")
    private List<OkexWalletBalance> data;

    @JsonProperty("arg")
    private OkExStreamArgInstument arg;
}
