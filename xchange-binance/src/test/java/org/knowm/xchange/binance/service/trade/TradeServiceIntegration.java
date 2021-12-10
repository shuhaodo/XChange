package org.knowm.xchange.binance.service.trade;

import static org.knowm.xchange.binance.dto.trade.OrderType.LIMIT;
import static org.knowm.xchange.binance.dto.trade.OrderType.MARKET;
import static org.knowm.xchange.binance.dto.trade.OrderType.STOP_LOSS_LIMIT;
import static org.knowm.xchange.binance.dto.trade.OrderType.TAKE_PROFIT_LIMIT;
import static org.knowm.xchange.dto.Order.OrderType.ASK;
import static org.knowm.xchange.dto.Order.OrderType.BID;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchange.binance.BinanceExchangeIntegration;
import org.knowm.xchange.binance.dto.trade.BinanceNewOrder;
import org.knowm.xchange.binance.dto.trade.BinanceOrderFlags;
import org.knowm.xchange.binance.dto.trade.TimeInForce;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.binance.service.BinanceCancelOrderParams;

public class TradeServiceIntegration extends BinanceExchangeIntegration {

  static BinanceTradeService tradeService;
  private final Order.IOrderFlags clientOrderId = BinanceTradeService.BinanceOrderFlags.withClientId("apple");

  public static void main(String[] args) throws Exception {
    TradeServiceIntegration test = new TradeServiceIntegration();
    test.createExchange();
    tradeService = (BinanceTradeService) exchange.getTradeService();
    test.placeMarginLimitOrder();
  }

  public void placeMarginMarketOrder() throws Exception {
    String orderId = tradeService.placeMarketMarginOrder(sampleMarketOrder());
    OpenOrders orders = tradeService.getOpenMarginOrders();
    System.out.println("market order: " + orderId + " orders: " + orders);
  }

  private void placeMarginLimitOrder() throws Exception {
    final LimitOrder limitOrder = sampleLimitOrder();
    String orderId = tradeService.placeLimitMarginOrder(limitOrder);
    OpenOrders orders = tradeService.getOpenMarginOrders();
    System.out.println("limit order: " + orderId + " orders: " + orders);
    //tradeService.cancelMarginOrder(new BinanceCancelOrderParams(limitOrder.getCurrencyPair(), orderId));
  }

  private LimitOrder sampleLimitOrder() throws IOException {
    final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;
    final BigDecimal amount = BigDecimal.valueOf(0.1);
    final BigDecimal limitPrice = BigDecimal.valueOf(170.0);
    return new LimitOrder.Builder(ASK, currencyPair)
        .originalAmount(amount)
        .limitPrice(limitPrice)
        .flag(clientOrderId)
        .build();
  }
  private MarketOrder sampleMarketOrder() {
    final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;
    final BigDecimal amount = BigDecimal.valueOf(0.11);
    return new MarketOrder.Builder(BID, currencyPair)
            .originalAmount(amount)
            .flag(clientOrderId)
            .build();
  }
}
