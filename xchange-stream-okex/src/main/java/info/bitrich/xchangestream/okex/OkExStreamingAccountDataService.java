package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.okex.dto.OkExOrderbookStreamStream;
import info.bitrich.xchangestream.okex.dto.OkExStreamAccountDataResponse;
import info.bitrich.xchangestream.okex.dto.OkExStreamOrderbookResponse;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.okex.v5.OkexAdapters;
import org.knowm.xchange.okex.v5.dto.account.OkexWalletBalance;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexOrderbook;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class OkExStreamingAccountDataService implements StreamingAccountService {
    private final OkExStreamingService service;

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
    private final Map<String, OkExOrderbookStreamStream> orderbooks = new HashMap<>();
    private final String KEY_CHANNEL = "channel";
    private final String CHANNEL_ACCOUNT = "account";

  OkExStreamingAccountDataService(OkExStreamingService service) {
    this.service = service;
  }

  /**
   *
   * @return
   */
  @Override
  public Observable<List<Balance>> getBalanceChanges() {
    List<Map<String, String>> channels = new ArrayList<>();
      Map<String, String> map = new HashMap<>();
      map.put(KEY_CHANNEL, CHANNEL_ACCOUNT);
      channels.add(map);
    return service
        .subscribeChannel(CHANNEL_ACCOUNT, channels)
        .map(
            s -> {
                try {
                    OkExStreamAccountDataResponse response = mapper.treeToValue(s, OkExStreamAccountDataResponse.class);
                    if (response == null || response.getData() == null || response.getData().size() < 1) {
                        return new ArrayList<>();
                    }
                    OkexWalletBalance okexWallet = response.getData().get(0);
                    return Arrays.stream(okexWallet.getDetails())
                            .map(
                                    detail ->
                                            new Balance.Builder()
                                                    .currency(new Currency(detail.getCurrency()))
                                                    .total(new BigDecimal(detail.getCashBalance()))
                                                    .available(new BigDecimal(detail.getAvilableEquity()))
                                                    .interest(new BigDecimal(detail.getInterest()))
                                                    .frozen(new BigDecimal(detail.getFrozenBalance()))
                                                    .timestamp(new Date())
                                                    .build())
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            });
  }
}
