package org.knowm.xchange.binance.service.marketdata;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchange.binance.BinanceExchangeIntegration;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class MarketDataServiceIntegration extends BinanceExchangeIntegration {

  static MarketDataService marketService;

  @BeforeClass
  public static void beforeClass() throws Exception {
    createExchange();
    marketService = exchange.getMarketDataService();
  }

  @Before
  public void before() {
    Assume.assumeNotNull(exchange.getExchangeSpecification().getApiKey());
  }

  @Test
  public void testTimestamp() throws Exception {

    long serverTime = exchange.getTimestampFactory().createValue();
    Assert.assertTrue(0 < serverTime);
  }

  @Test
  public void testBinanceMarginPairs() throws Exception {
    BinanceMarketDataService service = (BinanceMarketDataService) marketService;
    List<CurrencyPair> pairs = service.getMarginTradingPairs();
    Assert.assertTrue(!pairs.isEmpty());
    System.out.println(pairs.size() + " margin pairs");
    ExchangeMetaData meta = exchange.getExchangeMetaData();
    List<CurrencyPairMetaData> margins = meta.getCurrencyPairs().values().stream()
            .filter( at -> at.isMarginOrderEnabled())
            .collect(Collectors.toList());
    Assert.assertNotEquals(meta.getCurrencyPairs().size(), margins.size());
  }

  @Test
  public void testBinanceMarginCurrencies() throws Exception {
    BinanceMarketDataService service = (BinanceMarketDataService) marketService;
    Map<Currency, CurrencyMetaData> meta = service.getAllCurrencies();
    List<CurrencyMetaData> marginCurrencies = meta.values().stream()
            .filter(at -> at.isBorrowable() && BigDecimal.ZERO.compareTo(at.getInterest()) < 0)
            .collect(Collectors.toList());
    List<CurrencyMetaData> withDrawAllowed = meta.values().stream()
            .filter(at -> at.isWithdrawAllowed())
            .collect(Collectors.toList());
    List<CurrencyMetaData> depositAllowed = meta.values().stream()
            .filter(at -> at.isDepositAllowed())
            .collect(Collectors.toList());
    Assert.assertTrue(!marginCurrencies.isEmpty());
    Assert.assertTrue(!withDrawAllowed.isEmpty());
    Assert.assertTrue(!depositAllowed.isEmpty());
    Assert.assertNotEquals(marginCurrencies.size(), meta.size());
    Assert.assertNotEquals(withDrawAllowed.size(), meta.size());
    Assert.assertNotEquals(depositAllowed.size(), meta.size());
    System.out.println(meta.size() + " currencies");
    System.out.println(marginCurrencies.size() + " margin currencies");
    System.out.println(withDrawAllowed.size() + " currencies allow withdraw");
    System.out.println(depositAllowed.size() + " currencies allow deposit");

    Collection<CurrencyMetaData> metaData = exchange.getExchangeMetaData().getCurrencies().values();
    int marginCnt = metaData.stream().filter(it -> it.isBorrowable() && it.getInterest().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList()).size();
    int withdrawCnt = metaData.stream().filter(it -> it.isWithdrawAllowed())
            .collect(Collectors.toList()).size();
    int depositCnt = metaData.stream().filter(it -> it.isDepositAllowed())
            .collect(Collectors.toList()).size();
    Assert.assertTrue(marginCnt <= marginCurrencies.size() && marginCnt > 0);
    Assert.assertTrue(withdrawCnt <= withDrawAllowed.size() && withdrawCnt > 0);
    Assert.assertTrue(depositCnt <= depositAllowed.size() && depositCnt > 0);
  }
/*
  @Test
  public void testBinanceTicker24h() throws Exception {

    List<BinanceTicker24h> tickers = new ArrayList<>();
    for (CurrencyPair cp : exchange.getExchangeMetaData().getCurrencyPairs().keySet()) {
      if (cp.counter == Currency.USDT) {
        tickers.add(getBinanceTicker24h(cp));
      }
    }

    Collections.sort(
        tickers,
        (BinanceTicker24h t1, BinanceTicker24h t2) ->
            t2.getPriceChangePercent().compareTo(t1.getPriceChangePercent()));

    tickers.stream()
        .forEach(
            t -> {
              System.out.println(
                  t.getCurrencyPair()
                      + " => "
                      + String.format("%+.2f%%", t.getPriceChangePercent()));
            });
  }

  private BinanceTicker24h getBinanceTicker24h(CurrencyPair pair) throws IOException {
    BinanceMarketDataService service = (BinanceMarketDataService) marketService;
    return service.ticker24h(pair);
  }

 */
}
