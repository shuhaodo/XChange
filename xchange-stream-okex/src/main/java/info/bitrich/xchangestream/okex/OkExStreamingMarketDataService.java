package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.okex.dto.OkExOrderbookStreamStream;
import info.bitrich.xchangestream.okex.dto.OkExStreamOrderbookResponse;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;

import java.util.*;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexOrderbook;

public class OkExStreamingMarketDataService implements StreamingMarketDataService {
    private final OkExStreamingService service;

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
    private final Map<String, OkExOrderbookStreamStream> orderbooks = new HashMap<>();
    private final String KEY_INST_ID = "instId";
    private final String KEY_CHANNEL = "channel";
    private final String BOOK_CHANNEL = "books50-l2-tbt";

  OkExStreamingMarketDataService(OkExStreamingService service) {
    this.service = service;
  }

  /**
   *
   * @param currencyPairs Currency pairs of the order book
   * @return
   */
  @Override
  public Observable<OrderBook> getOrderBooks(Set<CurrencyPair> currencyPairs, Object... args) {
    List<Map<String, String>> channels = new ArrayList<>();
    for (CurrencyPair pair: currencyPairs) {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_CHANNEL, BOOK_CHANNEL);
        map.put(KEY_INST_ID, pair.base.toString() + "-" + pair.counter.toString());
        channels.add(map);
    }
    return service
        .subscribeChannel(BOOK_CHANNEL, channels)
        .map(
            s -> {
                OrderBook emptyBook = new OrderBook(new Date(), new ArrayList<>(), new ArrayList<>());
                try {
                    OkExOrderbookStreamStream okExOrderbookStream;
                    OkExStreamOrderbookResponse response = mapper.treeToValue(s, OkExStreamOrderbookResponse.class);
                    if (response == null || response.getData() == null || response.getData().isEmpty()) {
                        return emptyBook;
                    }
                    String action = response.getAction();
                    String pair = response.getArg().getInstrument();
                    OkexOrderbook book = response.getData().get(0);
                    if (!orderbooks.containsKey(pair) || action.equals("snapshot")) {
                        okExOrderbookStream = new OkExOrderbookStreamStream(book, new CurrencyPair(pair));
                        orderbooks.put(pair, okExOrderbookStream);
                    } else {
                        okExOrderbookStream = orderbooks.get(pair);
                        okExOrderbookStream.updateLevels(book);
                    }

                    return okExOrderbookStream.toOrderbook();
                } catch (Exception e) {
                    e.printStackTrace();
                    return emptyBook;
                }
            });
  }
}
