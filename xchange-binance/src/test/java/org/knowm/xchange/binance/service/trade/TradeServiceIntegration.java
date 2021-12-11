package org.knowm.xchange.binance.service.trade;

import static org.knowm.xchange.dto.Order.OrderType.ASK;
import static org.knowm.xchange.dto.Order.OrderType.BID;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

import org.knowm.xchange.binance.BinanceExchangeIntegration;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParams;
import org.knowm.xchange.service.trade.params.orders.DefaultQueryOrderParams;

public class TradeServiceIntegration extends BinanceExchangeIntegration {

  static BinanceTradeService tradeService;
  private final String clientOrderId = "apple";
  private final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;

  public static void main(String[] args) throws Exception {
    TradeServiceIntegration test = new TradeServiceIntegration();
    test.createExchange();
    tradeService = (BinanceTradeService) exchange.getTradeService();
    //test.placeMarginMarketOrder(true);
    //test.placeMarginMarketOrder(false);
  }

  public void placeMarginMarketOrder(boolean isMargin) throws Exception {
    MarketOrder buyOrder = sampleMarketOrder(BID);
    MarketOrder sellOrder = sampleMarketOrder(ASK);
    if (isMargin) {
      buyOrder.addOrderFlag(Order.TradeModeFlags.MARGIN);
      sellOrder.addOrderFlag(Order.TradeModeFlags.MARGIN);
    }

    String orderId = tradeService.placeMarketOrder(buyOrder);
    OpenOrders openOrders = tradeService.getOpenOrders();
    System.out.println("openOrders: " + openOrders);
    Collection<Order> orders = tradeService.getOrder(new DefaultQueryOrderParams(
            currencyPair, orderId, clientOrderId, isMargin));
    System.out.println("market buy, margin? " + isMargin
            + ", order Id: " + orderId + " order: " + orders.stream().findFirst());

    orderId = tradeService.placeMarketOrder(sellOrder);
    openOrders = tradeService.getOpenOrders();
    System.out.println("openOrders: " + openOrders);
    orders = tradeService.getOrder(new DefaultQueryOrderParams(
            currencyPair, orderId, clientOrderId, isMargin));
    System.out.println("market sell, margin? " + isMargin
            + ", order Id: " + orderId + " order: " + orders.stream().findFirst());
  }

  private void placeMarginLimitOrder() throws Exception {
    final LimitOrder limitOrder = sampleLimitOrder();
    limitOrder.addOrderFlag(Order.TradeModeFlags.MARGIN);
    String orderId = tradeService.placeLimitOrder(limitOrder);
    OpenOrders openOrders = tradeService.getOpenOrders();
    System.out.println("openOrders: " + openOrders);
    Collection<Order> orders = tradeService.getOrder(new DefaultQueryOrderParams(
            currencyPair, null, clientOrderId, true));
    System.out.println("Limit orderId: " + orderId + " order:"  + orders.stream().findFirst());
    tradeService.cancelOrder(
            new DefaultCancelOrderParams(currencyPair, null, clientOrderId, true));
  }

  private LimitOrder sampleLimitOrder() throws IOException {
    final BigDecimal amount = BigDecimal.valueOf(0.1);
    final BigDecimal limitPrice = BigDecimal.valueOf(170.0);
    return new LimitOrder.Builder(ASK, currencyPair)
        .originalAmount(amount)
        .limitPrice(limitPrice)
        .userReference(clientOrderId)
        .build();
  }
  private MarketOrder sampleMarketOrder(Order.OrderType side) {
    final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;
    final BigDecimal amount = BigDecimal.valueOf(0.10);
    return new MarketOrder.Builder(side, currencyPair)
            .originalAmount(amount)
            .userReference(clientOrderId)
            .build();
  }
}
