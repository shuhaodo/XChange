package org.knowm.xchange.okex.v5;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.okex.v5.dto.OkexResponse;
import org.knowm.xchange.okex.v5.dto.account.OkexAssetBalance;
import org.knowm.xchange.okex.v5.dto.account.OkexWalletBalance;
import org.knowm.xchange.okex.v5.dto.marketdata.*;
import org.knowm.xchange.okex.v5.dto.trade.OkexAmendOrderRequest;
import org.knowm.xchange.okex.v5.dto.trade.OkexOrderDetails;
import org.knowm.xchange.okex.v5.dto.trade.OkexOrderRequest;

/** Author: Max Gao (gaamox@tutanota.com) Created: 08-06-2021 */
public class OkexAdapters {

  private static final String TRADING_WALLET_ID = "trading";
  private static final String FOUNDING_WALLET_ID = "founding";

  public static Order adaptOrder(OkexOrderDetails order) {
    return new LimitOrder(
        "buy".equals(order.getSide()) ? Order.OrderType.BID : Order.OrderType.ASK,
        new BigDecimal(order.getAmount()),
        new CurrencyPair(order.getInstrumentId()),
        order.getOrderId(),
        new Date(Long.parseLong(order.getCreationTime())),
        new BigDecimal(order.getPrice()),
        order.getAverageFilledPrice().isEmpty()
            ? BigDecimal.ZERO
            : new BigDecimal(order.getAverageFilledPrice()),
        new BigDecimal(order.getAccumulatedFill()),
        new BigDecimal(order.getFee()),
        "live".equals(order.getState())
            ? Order.OrderStatus.OPEN
            : Order.OrderStatus.PARTIALLY_FILLED,
        order.getClientOrderId());
  }

  public static OpenOrders adaptOpenOrders(List<OkexOrderDetails> orders) {
    List<LimitOrder> openOrders =
        orders.stream()
            .map(
                order ->
                    new LimitOrder(
                        "buy".equals(order.getSide()) ? Order.OrderType.BID : Order.OrderType.ASK,
                        new BigDecimal(order.getAmount()),
                        new CurrencyPair(order.getInstrumentId()),
                        order.getOrderId(),
                        new Date(Long.parseLong(order.getCreationTime())),
                        new BigDecimal(order.getPrice()),
                        order.getAverageFilledPrice().isEmpty()
                            ? BigDecimal.ZERO
                            : new BigDecimal(order.getAverageFilledPrice()),
                        new BigDecimal(order.getAccumulatedFill()),
                        new BigDecimal(order.getFee()),
                        "live".equals(order.getState())
                            ? Order.OrderStatus.OPEN
                            : Order.OrderStatus.PARTIALLY_FILLED,
                        order.getClientOrderId()))
            .collect(Collectors.toList());
    return new OpenOrders(openOrders);
  }

  public static OkexAmendOrderRequest adaptAmendOrder(LimitOrder order) {
    return OkexAmendOrderRequest.builder()
        .instrumentId(adaptCurrencyPairId((CurrencyPair) order.getInstrument()))
        .orderId(order.getId())
        .clientOrderId(order.getUserReference())
        .amendedAmount(order.getOriginalAmount().toString())
        .amendedPrice(order.getLimitPrice().toString())
        .build();
  }

  public static OkexOrderRequest adaptOrder(Order order) {
    String orderType = "limit";
    String price = null;
    if (order instanceof LimitOrder) {
      price = ((LimitOrder) order).getLimitPrice().toString();
    } else {
      orderType = "market";
    }
    return OkexOrderRequest.builder()
        .instrumentId(adaptInstrumentId(order.getInstrument()))
        .side(order.getType() == Order.OrderType.BID ? "buy" : "sell")
        .posSide(null) // PosSide should come as a input from an extended LimitOrder class to
        // support Futures/Swap capabilities of Okex, till then it should be null to
        // perform "net" orders
        .orderType(orderType)
        .amount(order.getOriginalAmount().toString())
        .price(price)
        .tradeMode("cross")
        .targetCurrency("base_ccy") //use base quantity in market order
        .clientOrderId(order.getUserReference())
        .build();
  }

  public static OrderBook adaptOrderBook(
      OkexResponse<List<OkexOrderbook>> okexOrderbook, Instrument instrument) {

    List<LimitOrder> asks = new ArrayList<>();
    List<LimitOrder> bids = new ArrayList<>();

    okexOrderbook
        .getData()
        .get(0)
        .getAsks()
        .forEach(
            okexAsk ->
                asks.add(
                    adaptOrderbookOrder(
                        okexAsk.getVolume(), okexAsk.getPrice(), instrument, OrderType.ASK)));

    okexOrderbook
        .getData()
        .get(0)
        .getBids()
        .forEach(
            okexBid ->
                bids.add(
                    adaptOrderbookOrder(
                        okexBid.getVolume(), okexBid.getPrice(), instrument, OrderType.BID)));

    return new OrderBook(Date.from(Instant.now()), asks, bids);
  }

  public static LimitOrder adaptOrderbookOrder(
      BigDecimal amount, BigDecimal price, Instrument instrument, Order.OrderType orderType) {

    return new LimitOrder(orderType, amount, instrument, "", null, price);
  }

  public static String adaptCurrencyPairId(Instrument instrument) {
    return instrument.toString().replace('/', '-');
  }

  public static String adaptInstrumentId(Instrument instrument) {
    return adaptCurrencyPairId(instrument);
  }

  public static String adaptCurrencyPairId(CurrencyPair currencyPair) {
    return currencyPair.toString().replace('/', '-');
  }

  public static Trades adaptTrades(List<OkexTrade> okexTrades, Instrument instrument) {
    List<Trade> trades = new ArrayList<>();

    okexTrades.forEach(
        okexTrade ->
            trades.add(
                new Trade.Builder()
                    .id(okexTrade.getTradeId())
                    .instrument(instrument)
                    .originalAmount(okexTrade.getSz())
                    .price(okexTrade.getPx())
                    .timestamp(okexTrade.getTs())
                    .type(adaptOkexOrderSideToOrderType(okexTrade.getSide()))
                    .build()));

    return new Trades(trades);
  }

  public static List<Ticker> adaptTickers(List<OkexTicker> tickers) {
    return tickers.stream().map(
            t -> new Ticker.Builder()
                    .instrument(new CurrencyPair(t.getInstId()))
                    .open(t.getOpen24h())
                    .last(t.getLast())
                    .bid(t.getBidPx())
                    .ask(t.getAskPx())
                    .high(t.getHigh24h())
                    .low(t.getLow24h())
                    .volume(t.getVol24h())
                    .quoteVolume(t.getVolCcy24h())
                    .bidSize(t.getBidSz())
                    .askSize(t.getAskSz())
                    .timestamp(new Date(t.getTs()))
                    .build()
    ).collect(Collectors.toList());
  }

  public static Order.OrderType adaptOkexOrderSideToOrderType(String okexOrderSide) {

    return okexOrderSide.equals("buy") ? Order.OrderType.BID : Order.OrderType.ASK;
  }

  private static Currency adaptCurrency(OkexCurrency currency) {
    return new Currency(currency.getCurrency());
  }

  public static CurrencyPair adaptCurrencyPair(OkexInstrument instrument) {
    return new CurrencyPair(instrument.getBaseCurrency(), instrument.getQuoteCurrency());
  }

  private static int numberOfDecimals(BigDecimal value) {
    double d = value.doubleValue();
    return -(int) Math.round(Math.log10(d));
  }

  public static ExchangeMetaData adaptToExchangeMetaData(
          ExchangeMetaData exchangeMetaData,
          List<OkexInstrument> instruments,
          List<OkexInstrument> marginInstruments,
          List<OkexCurrency> currs,
          List<OkexInterestRate> interestRates) {

    Map<CurrencyPair, CurrencyPairMetaData> currencyPairs =
        exchangeMetaData.getCurrencyPairs() == null
            ? new HashMap<>()
            : exchangeMetaData.getCurrencyPairs();

    Set<CurrencyPair> marginPairs = marginInstruments == null
            ? new HashSet<>()
            : new HashSet<>(marginInstruments.stream().map(OkexAdapters::adaptCurrencyPair).collect(Collectors.toList()));

    Map<Currency, CurrencyMetaData> currencies =
        exchangeMetaData.getCurrencies() == null
            ? new HashMap<>()
            : exchangeMetaData.getCurrencies();

    Map<Currency, BigDecimal> rates = new HashMap<>();

    for (OkexInstrument instrument : instruments) {
      if (!"live".equals(instrument.getState())) {
        continue;
      }
      CurrencyPair pair = adaptCurrencyPair(instrument);

      CurrencyPairMetaData staticMetaData = currencyPairs.get(pair);
      int priceScale = numberOfDecimals(new BigDecimal(instrument.getTickSize()));

      currencyPairs.put(
          pair,
          new CurrencyPairMetaData(
              new BigDecimal("0.50"),
              new BigDecimal(instrument.getMinSize()),
              null,
              null,
              null,
              null,
              priceScale,
              null,
              staticMetaData != null ? staticMetaData.getFeeTiers() : null,
              null,
              pair.counter,
              true,
              marginPairs.contains(pair))); //if margin trade is supported
    }

    if (interestRates != null) {
      interestRates.stream().forEach(rate -> {
        if (rate.getRate() != null) {
          Currency key = new Currency(rate.getCurrency());
          rates.put(key, rate.getRate());
        }
      });
    }

    if (currs != null) {
      currs.stream()
          .forEach(
              currency -> {
                Currency key = adaptCurrency(currency);
                CurrencyMetaData metaData = new CurrencyMetaData(
                        new BigDecimal(currency.getMaxFee()),
                        new BigDecimal(currency.getMinWd()),
                        currency.isCanWd(),
                        currency.isCanDep());
                BigDecimal rate = rates.get(key);
                if (rate != null) {
                  metaData.setIsBorrowable(true);
                  metaData.setInterest(rate);
                }
                currencies.put(key, metaData);
              });
    }

    return new ExchangeMetaData(
        currencyPairs,
        currencies,
        exchangeMetaData.getPublicRateLimits(),
        exchangeMetaData.getPrivateRateLimits(),
        true);
  }

  public static Wallet adaptOkexBalances(List<OkexWalletBalance> okexWalletBalanceList) {
    List<Balance> balances = new ArrayList<>();
    if (!okexWalletBalanceList.isEmpty()) {
      OkexWalletBalance okexWalletBalance = okexWalletBalanceList.get(0);
      balances =
          Arrays.stream(okexWalletBalance.getDetails())
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

    }

    return Wallet.Builder.from(balances)
        .id(TRADING_WALLET_ID)
        .features(new HashSet<>(Collections.singletonList(Wallet.WalletFeature.TRADING)))
        .build();
  }

  public static Wallet adaptOkexAssetBalances(List<OkexAssetBalance> okexAssetBalanceList) {
    List<Balance> balances;
    balances =
        okexAssetBalanceList.stream()
            .map(
                detail ->
                    new Balance.Builder()
                        .currency(new Currency(detail.getCurrency()))
                        .total(new BigDecimal(detail.getBalance()))
                        .available(new BigDecimal(detail.getAvailableBalance()))
                        .timestamp(new Date())
                        .build())
            .collect(Collectors.toList());

    return Wallet.Builder.from(balances)
        .id(FOUNDING_WALLET_ID)
        .features(new HashSet<>(Collections.singletonList(Wallet.WalletFeature.FUNDING)))
        .build();
  }
}
