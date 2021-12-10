package info.bitrich.xchangestream.okex;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Created by Lukas Zaoralek on 17.11.17. */
public class OkExManualExample {
  private static final Logger LOG = LoggerFactory.getLogger(OkExManualExample.class);

  public static void main(String[] args) {
    StreamingExchange exchange =
        StreamingExchangeFactory.INSTANCE.createExchange(OkExStreamingExchange.class);
    exchange.connect().blockingAwait();

    CurrencyPair btcUsdt = new CurrencyPair(new Currency("BTM"), new Currency("USDT"));
    Set<CurrencyPair> pairs = new HashSet<>();
    pairs.add(btcUsdt);
    pairs.add(CurrencyPair.ADA_USDT);
    pairs.add(CurrencyPair.LTC_USDT);
    exchange
        .getStreamingMarketDataService()
        .getOrderBooks(pairs)
        .subscribe(
            orderBook -> {
              //LOG.info("First ask: {}", orderBook.getAsks().get(0));
              //LOG.info("First bid: {}", orderBook.getBids().get(0));
            },
            throwable -> LOG.error("ERROR in getting order book: ", throwable));
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
