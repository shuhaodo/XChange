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

  @BeforeClass
  public static void beforeClass() throws Exception {
    createExchange();
    tradeService = (BinanceTradeService) exchange.getTradeService();
  }

  @Before
  public void before() {
    Assume.assumeNotNull(exchange.getExchangeSpecification().getApiKey());
  }

//  @Test
//  public void testPlaceMarginMarketOrder() throws Exception {
//    String orderId = tradeService.placeMarketMarginOrder(sampleMarketOrder());
//    OpenOrders orders = tradeService.getOpenMarginOrders();
//    System.out.println("order: " + orderId + " orders: " + orders);
//  }
//
//  @Test
//  public void testPlaceMarginLimitOrder() throws Exception {
//    final LimitOrder limitOrder = sampleLimitOrder();
//    String orderId = tradeService.placeLimitMarginOrder(limitOrder);
//    OpenOrders orders = tradeService.getOpenMarginOrders();
//    System.out.println("order: " + orderId + " orders: " + orders);
//    //tradeService.cancelMarginOrder(new BinanceCancelOrderParams(limitOrder.getCurrencyPair(), orderId));
  }

//  @Test
//  public void testPlaceTestOrderLimitOrderShouldNotThrowAnyException() throws IOException {
//    final LimitOrder limitOrder = sampleLimitOrder();
//
//    tradeService.placeTestOrder(LIMIT, limitOrder, limitOrder.getLimitPrice(), null);
//  }
//
  private LimitOrder sampleLimitOrder() throws IOException {
    final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;
    final BigDecimal amount = BigDecimal.valueOf(0.1);
    final BigDecimal limitPrice = BigDecimal.valueOf(170.0);
    return new LimitOrder.Builder(BID, currencyPair)
        .originalAmount(amount)
        .limitPrice(limitPrice)
        .flag(clientOrderId)
        .build();
  }
  private MarketOrder sampleMarketOrder() {
    final CurrencyPair currencyPair = CurrencyPair.ZEC_USDT;
    final BigDecimal amount = BigDecimal.valueOf(0.1);
    return new MarketOrder.Builder(ASK, currencyPair)
            .originalAmount(amount)
            .flag(clientOrderId)
            .build();
  }

//
//  private BigDecimal limitPriceForCurrencyPair(CurrencyPair currencyPair) throws IOException {
//    return exchange
//        .getMarketDataService()
//        .getOrderBook(currencyPair)
//        .getAsks()
//        .get(0)
//        .getLimitPrice();
//  }
//
//  @Test
//  public void testPlaceTestOrderMarketOrderShouldNotThrowAnyException() throws IOException {
//    final MarketOrder marketOrder = sampleMarketOrder();
//
//    tradeService.placeTestOrder(MARKET, marketOrder, null, null);
//  }
//


//  @Test
//  public void testPlaceTestOrderStopLossLimitOrderShouldNotThrowAnyException() throws IOException {
//    final StopOrder stopLimitOrder = sampleStopLimitOrder();
//
//    tradeService.placeTestOrder(
//        STOP_LOSS_LIMIT,
//        stopLimitOrder,
//        stopLimitOrder.getLimitPrice(),
//        stopLimitOrder.getStopPrice());
//  }
//
//  private StopOrder sampleStopLimitOrder() throws IOException {
//    final CurrencyPair currencyPair = CurrencyPair.BTC_USDT;
//    final BigDecimal amount = BigDecimal.ONE;
//    final BigDecimal limitPrice = limitPriceForCurrencyPair(currencyPair);
//    final BigDecimal stopPrice =
//        limitPrice.multiply(new BigDecimal("0.9")).setScale(2, RoundingMode.HALF_UP);
//    return new StopOrder.Builder(BID, currencyPair)
//        .originalAmount(amount)
//        .limitPrice(limitPrice)
//        .stopPrice(stopPrice)
//        .intention(StopOrder.Intention.STOP_LOSS)
//        .flag(TimeInForce.GTC)
//        .build();
//  }
//
//  @Test
//  public void testPlaceTestOrderTakeProfitLimitOrderShouldNotThrowAnyException()
//      throws IOException {
//    final StopOrder takeProfitLimitOrder = sampleTakeProfitLimitOrder();
//
//    tradeService.placeTestOrder(
//        TAKE_PROFIT_LIMIT,
//        takeProfitLimitOrder,
//        takeProfitLimitOrder.getLimitPrice(),
//        takeProfitLimitOrder.getStopPrice());
//  }
//
//  private StopOrder sampleTakeProfitLimitOrder() throws IOException {
//    final CurrencyPair currencyPair = CurrencyPair.BTC_USDT;
//    final BigDecimal amount = BigDecimal.ONE;
//    final BigDecimal limitPrice = limitPriceForCurrencyPair(currencyPair);
//    final BigDecimal takeProfitPrice =
//        limitPrice.multiply(new BigDecimal("1.1")).setScale(2, RoundingMode.HALF_UP);
//    return new StopOrder.Builder(BID, currencyPair)
//        .originalAmount(amount)
//        .stopPrice(takeProfitPrice)
//        .limitPrice(limitPrice)
//        .intention(StopOrder.Intention.TAKE_PROFIT)
//        .flag(TimeInForce.GTC)
//        .build();
//  }
}
