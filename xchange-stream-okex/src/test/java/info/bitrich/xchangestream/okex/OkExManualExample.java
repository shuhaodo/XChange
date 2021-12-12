package info.bitrich.xchangestream.okex;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
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
  private static StreamingExchange exchange = createExchange();


  public static void main(String[] args) {
    OkExManualExample me = new OkExManualExample();
    exchange.connect().blockingAwait();
    me.accountBalanceStream();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static StreamingExchange createExchange() {
    StreamingExchange exchange =
            StreamingExchangeFactory.INSTANCE.createExchangeWithoutSpecification(OkExStreamingExchange.class);
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();
    spec.setSecretKey(System.getenv("kex"));
    spec.setApiKey(System.getenv("api-key"));
    spec.setExchangeSpecificParametersItem("passphrase", System.getenv("xek"));
    exchange.applySpecification(spec);

    return exchange;
  }

  private void accountBalanceStream() {
    exchange.getStreamingAccountService().getBalanceChanges().subscribe(balances ->
            {
              LOG.info("balances: {}", balances);

            }
    );
  }

  private void orderbookStream() {
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
  }
}
