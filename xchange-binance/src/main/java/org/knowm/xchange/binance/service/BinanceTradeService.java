package org.knowm.xchange.binance.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceErrorAdapter;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.BinanceNewOrder;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.BinanceTrade;
import org.knowm.xchange.binance.dto.trade.OrderType;
import org.knowm.xchange.binance.dto.trade.TimeInForce;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.IOrderFlags;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.*;
import org.knowm.xchange.service.trade.params.orders.*;
import org.knowm.xchange.utils.Assert;

public class BinanceTradeService extends BinanceTradeServiceRaw implements TradeService {

  public BinanceTradeService(
      BinanceExchange exchange,
      BinanceAuthenticated binance,
      ResilienceRegistries resilienceRegistries) {
    super(exchange, binance, resilienceRegistries);
  }

  @Override
  public OpenOrders getOpenOrders() throws IOException {
    return getOpenOrders(new DefaultOpenOrdersParam());
  }

  public OpenOrders getOpenOrders(CurrencyPair pair) throws IOException {
    return getOpenOrders(new DefaultOpenOrdersParamCurrencyPair(pair));
  }

  @Override
  public OpenOrders getOpenOrders(OpenOrdersParams params) throws IOException {
    return doGetOpenOrders(params);
  }

  private OpenOrders doGetOpenOrders(OpenOrdersParams params) throws IOException {
    try {
      List<BinanceOrder> binanceOpenOrders;
      List<BinanceOrder> binanceMarginOrders;
      if (params instanceof OpenOrdersParamCurrencyPair) {
        OpenOrdersParamCurrencyPair pairParams = (OpenOrdersParamCurrencyPair) params;
        CurrencyPair pair = pairParams.getCurrencyPair();
        binanceOpenOrders = super.openOrders(pair);
        binanceMarginOrders = super.openMarginOrders(pair);
      } else {
        binanceOpenOrders = super.openOrders();
        binanceMarginOrders = super.openMarginOrders();
      }

      binanceOpenOrders.addAll(binanceMarginOrders);
      List<LimitOrder> limitOrders = new ArrayList<>();
      List<Order> otherOrders = new ArrayList<>();
      binanceOpenOrders.forEach(
          binanceOrder -> {
            Order order = BinanceAdapters.adaptOrder(binanceOrder);
            if (order instanceof LimitOrder) {
              limitOrders.add((LimitOrder) order);
            } else {
              otherOrders.add(order);
            }
          });
      return new OpenOrders(limitOrders, otherOrders);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  @Override
  public String placeMarketOrder(MarketOrder mo) throws IOException {
    if (mo.hasFlag(Order.TradeModeFlags.MARGIN)) {
      return placeMarginOrder(OrderType.MARKET, mo, null, null, null);
    } else {
      return placeOrder(OrderType.MARKET, mo, null, null, null);
    }
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    TimeInForce tif = timeInForceFromOrder(limitOrder).orElse(TimeInForce.GTC);
    OrderType type;
    if (limitOrder.hasFlag(org.knowm.xchange.binance.dto.trade.BinanceOrderFlags.LIMIT_MAKER)) {
      type = OrderType.LIMIT_MAKER;
      tif = null;
    } else {
      type = OrderType.LIMIT;
    }

    if (limitOrder.hasFlag(Order.TradeModeFlags.MARGIN)) {
      return placeMarginOrder(type, limitOrder, limitOrder.getLimitPrice(), null, tif);
    } else {
      return placeOrder(type, limitOrder, limitOrder.getLimitPrice(), null, tif);
    }
  }

  @Override
  public String placeStopOrder(StopOrder order) throws IOException {
    // Time-in-force should not be provided for market orders but is required for
    // limit orders, order we only default it for limit orders. If the caller
    // specifies one for a market order, we don't remove it, since Binance might
    // allow
    // it at some point.
    TimeInForce tif =
        timeInForceFromOrder(order).orElse(order.getLimitPrice() != null ? TimeInForce.GTC : null);

    OrderType orderType = BinanceAdapters.adaptOrderType(order);

    return placeOrder(orderType, order, order.getLimitPrice(), order.getStopPrice(), tif);
  }

  private Optional<TimeInForce> timeInForceFromOrder(Order order) {
    return order.getOrderFlags().stream()
        .filter(flag -> flag instanceof TimeInForce)
        .map(flag -> (TimeInForce) flag)
        .findFirst();
  }

  private String placeOrder(
      OrderType type, Order order, BigDecimal limitPrice, BigDecimal stopPrice, TimeInForce tif)
      throws IOException {
    try {
      Long recvWindow =
          (Long)
              exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");
      BinanceNewOrder newOrder =
          newOrder(
              order.getCurrencyPair(),
              BinanceAdapters.convert(order.getType()),
              type,
              tif,
              order.getOriginalAmount(),
              limitPrice,
              getClientOrderId(order),
              stopPrice,
              null,
              null);
      return Long.toString(newOrder.orderId);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  private String placeMarginOrder(
          OrderType type, Order order, BigDecimal limitPrice, BigDecimal stopPrice, TimeInForce tif)
          throws IOException {
    try {
      BinanceNewOrder newOrder =
              newMarginOrder(
                      order.getCurrencyPair(),
                      BinanceAdapters.convert(order.getType()),
                      type,
                      tif,
                      order.getOriginalAmount(),
                      limitPrice,
                      getClientOrderId(order),
                      stopPrice,
                      null,
                      null);
      return Long.toString(newOrder.orderId);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  public void placeTestOrder(
      OrderType type, Order order, BigDecimal limitPrice, BigDecimal stopPrice) throws IOException {
    try {
      TimeInForce tif = timeInForceFromOrder(order).orElse(null);
      Long recvWindow =
          (Long)
              exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");
      testNewOrder(
          order.getCurrencyPair(),
          BinanceAdapters.convert(order.getType()),
          type,
          tif,
          order.getOriginalAmount(),
          limitPrice,
          getClientOrderId(order),
          stopPrice,
          null);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  private String getClientOrderId(Order order) {

    return order.getUserReference();
  }

  @Override
  public boolean cancelOrder(String orderId) {
    throw new ExchangeException("You need to provide the currency pair to cancel an order.");
  }

  @Override
  public boolean cancelOrder(CancelOrderParams params) throws IOException {
    try {
      if (!(params instanceof DefaultCancelOrderParams)) {
        throw new ExchangeException(
            "params must be DefaultCancelOrderParams");
      }
      DefaultCancelOrderParams param = (DefaultCancelOrderParams) params;
      CurrencyPair pair = (CurrencyPair)param.getInstrument();
      if (param.getIsMarginOrder()) {
        super.cancelMarginOrder(
                pair,
                param.getOrderId(),
                param.getUserReference(),
                param.getUserReference());
      } else {
        super.cancelOrder(
                pair,
                param.getOrderId(),
                param.getUserReference(),
                param.getUserReference());
      }
      return true;
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    try {
      Assert.isTrue(
          params instanceof TradeHistoryParamCurrencyPair,
          "You need to provide the currency pair to get the user trades.");
      TradeHistoryParamCurrencyPair pairParams = (TradeHistoryParamCurrencyPair) params;
      CurrencyPair pair = pairParams.getCurrencyPair();
      if (pair == null) {
        throw new ExchangeException(
            "You need to provide the currency pair to get the user trades.");
      }
      Long orderId = null;
      Long startTime = null;
      Long endTime = null;
      if (params instanceof TradeHistoryParamsTimeSpan) {
        if (((TradeHistoryParamsTimeSpan) params).getStartTime() != null) {
          startTime = ((TradeHistoryParamsTimeSpan) params).getStartTime().getTime();
        }
        if (((TradeHistoryParamsTimeSpan) params).getEndTime() != null) {
          endTime = ((TradeHistoryParamsTimeSpan) params).getEndTime().getTime();
        }
      }
      Long fromId = null;
      if (params instanceof TradeHistoryParamsIdSpan) {
        TradeHistoryParamsIdSpan idParams = (TradeHistoryParamsIdSpan) params;
        try {
          fromId = BinanceAdapters.id(idParams.getStartId());
        } catch (Throwable ignored) {
        }
      }
      if ((fromId != null) && (startTime != null || endTime != null)) {
        throw new ExchangeException(
            "You should either specify the id from which you get the user trades from or start and end times. If you specify both, Binance will only honour the fromId parameter.");
      }

      Integer limit = null;
      if (params instanceof TradeHistoryParamLimit) {
        TradeHistoryParamLimit limitParams = (TradeHistoryParamLimit) params;
        limit = limitParams.getLimit();
      }

      List<BinanceTrade> binanceTrades =
          super.myTrades(pair, orderId, startTime, endTime, fromId, limit);
      List<UserTrade> trades =
          binanceTrades.stream()
              .map(
                  t ->
                      new UserTrade.Builder()
                          .type(BinanceAdapters.convertType(t.isBuyer))
                          .originalAmount(t.qty)
                          .currencyPair(pair)
                          .price(t.price)
                          .timestamp(t.getTime())
                          .id(Long.toString(t.id))
                          .orderId(Long.toString(t.orderId))
                          .feeAmount(t.commission)
                          .feeCurrency(Currency.getInstance(t.commissionAsset))
                          .build())
              .collect(Collectors.toList());
      long lastId = binanceTrades.stream().map(t -> t.id).max(Long::compareTo).orElse(0L);
      return new UserTrades(trades, lastId, Trades.TradeSortType.SortByTimestamp);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {

    return new BinanceTradeHistoryParams();
  }

  @Override
  public OpenOrdersParams createOpenOrdersParams() {

    return new DefaultOpenOrdersParamCurrencyPair();
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) {

    throw new NotAvailableFromExchangeException();
  }

  @Override
  public Collection<Order> getOrder(OrderQueryParams... params) throws IOException {
    try {
      Collection<Order> orders = new ArrayList<>();
      for (OrderQueryParams p : params) {
        if (!(p instanceof DefaultQueryOrderParams)) {
          throw new ExchangeException(
              "Parameters must be an instance of DefaultQueryOrderParams");
        }
        DefaultQueryOrderParams param = (DefaultQueryOrderParams) p;
        CurrencyPair currencyPair = (CurrencyPair) param.getInstrument();
        if (currencyPair == null
                || (param.getOrderId() == null && param.getClientId() == null)) {
          throw new ExchangeException(
              "You need to provide the currency pair and the order id or client id to query an order.");
        }

        BinanceOrder orderStatus;
        if (param.getIsMarginOrder()) {
          orderStatus = super.marginOrderStatus(
                  currencyPair,
                  param.getOrderId(),
                  param.getClientId());
        } else {
          orderStatus = super.orderStatus(
                  currencyPair,
                  param.getOrderId(),
                  param.getClientId());
        }
        orders.add(
            BinanceAdapters.adaptOrder(orderStatus));
      }
      return orders;
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  public interface BinanceOrderFlags extends IOrderFlags {

    static BinanceOrderFlags withClientId(String clientId) {
      return new ClientIdFlag(clientId);
    }

    /** Used in fields 'newClientOrderId' */
    String getClientId();
  }

  @Value
  static final class ClientIdFlag implements BinanceOrderFlags {
    private final String clientId;
  }
}
