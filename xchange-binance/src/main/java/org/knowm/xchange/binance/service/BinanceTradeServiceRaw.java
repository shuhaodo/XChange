package org.knowm.xchange.binance.service;

import static org.knowm.xchange.binance.BinanceResilience.*;
import static org.knowm.xchange.client.ResilienceRegistries.NON_IDEMPOTENT_CALLS_RETRY_CONFIG_NAME;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.*;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.CurrencyPair;

public class BinanceTradeServiceRaw extends BinanceBaseService {

  protected BinanceTradeServiceRaw(
      BinanceExchange exchange,
      BinanceAuthenticated binance,
      ResilienceRegistries resilienceRegistries) {
    super(exchange, binance, resilienceRegistries);
  }

  public BinanceNewOrder newMarginOrder(
          CurrencyPair pair,
          OrderSide side,
          OrderType type,
          TimeInForce timeInForce,
          BigDecimal quantity,
          BigDecimal price,
          String newClientOrderId,
          BigDecimal stopPrice,
          BigDecimal icebergQty,
          BinanceNewOrder.NewOrderResponseType newOrderRespType)
          throws IOException, BinanceException {
    OrderSideEffect sideEffect = side == OrderSide.SELL ? OrderSideEffect.MARGIN_BUY : OrderSideEffect.AUTO_REPAY;
    return decorateApiCall(
            () ->
                    binance.newMarginOrder(
                            BinanceAdapters.toSymbol(pair),
                            side,
                            type,
                            timeInForce,
                            quantity,
                            price,
                            newClientOrderId,
                            stopPrice,
                            icebergQty,
                            newOrderRespType,
                            sideEffect,
                            getRecvWindow(),
                            getTimestampFactory(),
                            apiKey,
                            signatureCreator))
            .withRetry(retry("newMarginOrder", NON_IDEMPOTENT_CALLS_RETRY_CONFIG_NAME))
            .withRateLimiter(rateLimiter(ORDERS_PER_SECOND_RATE_LIMITER))
            .withRateLimiter(rateLimiter(ORDERS_PER_DAY_RATE_LIMITER))
            .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
            .call();
  }

  public List<BinanceOrder> openMarginOrders() throws BinanceException, IOException {
    return openMarginOrders(null);
  }

  public List<BinanceOrder> openMarginOrders(CurrencyPair pair) throws BinanceException, IOException {
    return decorateApiCall(
            () ->
                    binance.openMarginOrders(
                            Optional.ofNullable(pair).map(BinanceAdapters::toSymbol).orElse(null),
                            getRecvWindow(),
                            getTimestampFactory(),
                            apiKey,
                            signatureCreator))
            .withRetry(retry("openMarginOrders"))
            .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER), openOrdersPermits(pair))
            .call();
  }

  public BinanceOrder marginOrderStatus(CurrencyPair pair, String orderId, String origClientOrderId)
          throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                    binance.marginOrderStatus(
                            BinanceAdapters.toSymbol(pair),
                            orderId,
                            origClientOrderId,
                            getRecvWindow(),
                            getTimestampFactory(),
                            super.apiKey,
                            super.signatureCreator))
            .withRetry(retry("marginOrderStatus"))
            .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
            .call();
  }

  public BinanceCancelledOrder cancelMarginOrder(
          CurrencyPair pair, String orderId, String origClientOrderId, String newClientOrderId)
          throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                    binance.cancelMarginOrder(
                            BinanceAdapters.toSymbol(pair),
                            orderId,
                            origClientOrderId,
                            newClientOrderId,
                            getRecvWindow(),
                            getTimestampFactory(),
                            super.apiKey,
                            super.signatureCreator))
            .withRetry(retry("cancelMarginOrder"))
            .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
            .call();
  }


  public List<BinanceOrder> openOrders() throws BinanceException, IOException {
    return openOrders(null);
  }

  public List<BinanceOrder> openOrders(CurrencyPair pair) throws BinanceException, IOException {
    return decorateApiCall(
            () ->
                binance.openOrders(
                    Optional.ofNullable(pair).map(BinanceAdapters::toSymbol).orElse(null),
                    getRecvWindow(),
                    getTimestampFactory(),
                    apiKey,
                    signatureCreator))
        .withRetry(retry("openOrders"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER), openOrdersPermits(pair))
        .call();
  }

  public BinanceNewOrder newOrder(
      CurrencyPair pair,
      OrderSide side,
      OrderType type,
      TimeInForce timeInForce,
      BigDecimal quantity,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      BigDecimal icebergQty,
      BinanceNewOrder.NewOrderResponseType newOrderRespType)
      throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                binance.newOrder(
                    BinanceAdapters.toSymbol(pair),
                    side,
                    type,
                    timeInForce,
                    quantity,
                    price,
                    newClientOrderId,
                    stopPrice,
                    icebergQty,
                    newOrderRespType,
                    getRecvWindow(),
                    getTimestampFactory(),
                    apiKey,
                    signatureCreator))
        .withRetry(retry("newOrder", NON_IDEMPOTENT_CALLS_RETRY_CONFIG_NAME))
        .withRateLimiter(rateLimiter(ORDERS_PER_SECOND_RATE_LIMITER))
        .withRateLimiter(rateLimiter(ORDERS_PER_DAY_RATE_LIMITER))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public void testNewOrder(
      CurrencyPair pair,
      OrderSide side,
      OrderType type,
      TimeInForce timeInForce,
      BigDecimal quantity,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      BigDecimal icebergQty)
      throws IOException, BinanceException {
    decorateApiCall(
            () ->
                binance.testNewOrder(
                    BinanceAdapters.toSymbol(pair),
                    side,
                    type,
                    timeInForce,
                    quantity,
                    price,
                    newClientOrderId,
                    stopPrice,
                    icebergQty,
                    getRecvWindow(),
                    getTimestampFactory(),
                    apiKey,
                    signatureCreator))
        .withRetry(retry("testNewOrder"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public BinanceOrder orderStatus(CurrencyPair pair, String orderId, String origClientOrderId)
      throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                binance.orderStatus(
                    BinanceAdapters.toSymbol(pair),
                    orderId,
                    origClientOrderId,
                    getRecvWindow(),
                    getTimestampFactory(),
                    super.apiKey,
                    super.signatureCreator))
        .withRetry(retry("orderStatus"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public BinanceCancelledOrder cancelOrder(
      CurrencyPair pair, String orderId, String origClientOrderId, String newClientOrderId)
      throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                binance.cancelOrder(
                    BinanceAdapters.toSymbol(pair),
                    orderId,
                    origClientOrderId,
                    newClientOrderId,
                    getRecvWindow(),
                    getTimestampFactory(),
                    super.apiKey,
                    super.signatureCreator))
        .withRetry(retry("cancelOrder"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public List<BinanceCancelledOrder> cancelAllOpenOrders(CurrencyPair pair)
      throws IOException, BinanceException {
    return decorateApiCall(
            () ->
                binance.cancelAllOpenOrders(
                    BinanceAdapters.toSymbol(pair),
                    getRecvWindow(),
                    getTimestampFactory(),
                    super.apiKey,
                    super.signatureCreator))
        .withRetry(retry("cancelAllOpenOrders"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public List<BinanceOrder> allOrders(CurrencyPair pair, Long orderId, Integer limit)
      throws BinanceException, IOException {
    return decorateApiCall(
            () ->
                binance.allOrders(
                    BinanceAdapters.toSymbol(pair),
                    orderId,
                    limit,
                    getRecvWindow(),
                    getTimestampFactory(),
                    apiKey,
                    signatureCreator))
        .withRetry(retry("allOrders"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public List<BinanceTrade> myTrades(
      CurrencyPair pair, Long orderId, Long startTime, Long endTime, Long fromId, Integer limit)
      throws BinanceException, IOException {
    return decorateApiCall(
            () ->
                binance.myTrades(
                    BinanceAdapters.toSymbol(pair),
                    orderId,
                    startTime,
                    endTime,
                    fromId,
                    limit,
                    getRecvWindow(),
                    getTimestampFactory(),
                    apiKey,
                    signatureCreator))
        .withRetry(retry("myTrades"))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER), myTradesPermits(limit))
        .call();
  }

  public BinanceListenKey startUserDataStream() throws IOException {
    return decorateApiCall(() -> binance.startUserDataStream(apiKey))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public void keepAliveDataStream(String listenKey) throws IOException {
    decorateApiCall(() -> binance.keepAliveUserDataStream(apiKey, listenKey))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public void closeDataStream(String listenKey) throws IOException {
    decorateApiCall(() -> binance.closeUserDataStream(apiKey, listenKey))
        .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  protected int openOrdersPermits(CurrencyPair pair) {
    return pair != null ? 1 : 40;
  }

  protected int myTradesPermits(Integer limit) {
    if (limit != null && limit > 500) {
      return 10;
    }
    return 5;
  }
}
