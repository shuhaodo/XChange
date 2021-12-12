package org.knowm.xchange.okex.v5;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.okex.v5.service.OkexAccountService;
import org.knowm.xchange.okex.v5.service.OkexMarketDataService;
import org.knowm.xchange.okex.v5.service.OkexTradeService;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParams;
import org.knowm.xchange.service.trade.params.orders.DefaultQueryOrderParams;


public class OkexExchangeIntegration {
  // Enter your authentication details here to run private endpoint tests
  private static final String API_KEY = System.getenv("api-key");
  private static final String SECRET_KEY = System.getenv("kex");
  private static final String PASSPHRASE = System.getenv("xek");

  protected static OkexExchange exchange;
  private final String clientOrderId = "testBTC0123";
  private final CurrencyPair pair = new CurrencyPair("ZEC/USDT");

  public static void main(String[] args) throws Exception {
    createExchange();
    OkexExchangeIntegration me = new OkexExchangeIntegration();
    //me.testAccountBalances();
    //me.testExchangeMetaData();
    //me.placeLimitOrders();
    me.placeMarketOrders();
  }

  public static void createExchange() throws Exception {
    exchange = ExchangeFactory.INSTANCE.createExchange(OkexExchange.class);
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();
    spec.setApiKey(API_KEY);
    spec.setSecretKey(SECRET_KEY);
    spec.setExchangeSpecificParametersItem("passphrase", PASSPHRASE);
    exchange.applySpecification(spec);
  }


  public void testAccountBalances() {
    try {
      OkexAccountService service = (OkexAccountService) exchange.getAccountService();
      AccountInfo info = service.getAccountInfo();
      System.out.println(info);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testExchangeMetaData() {
    OkexMarketDataService service = (OkexMarketDataService) exchange.getMarketDataService();
    Collection<CurrencyMetaData> metaData = exchange.getExchangeMetaData().getCurrencies().values();
    int marginCnt = metaData.stream().filter(it -> it.isBorrowable() && it.getInterest().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList()).size();
    int withdrawCnt = metaData.stream().filter(it -> it.isWithdrawAllowed())
            .collect(Collectors.toList()).size();
    int depositCnt = metaData.stream().filter(it -> it.isDepositAllowed())
            .collect(Collectors.toList()).size();
    System.out.println(metaData.size() + " currencies");
    System.out.println(marginCnt + " margin currencies");
    System.out.println(withdrawCnt + " currencies allow withdraw");
    System.out.println(depositCnt + " currencies allow deposit");
    Assert.assertTrue(marginCnt <= metaData.size() && marginCnt > 0);
    Assert.assertTrue(withdrawCnt <= metaData.size() && withdrawCnt > 0);
    Assert.assertTrue(depositCnt <= metaData.size() && depositCnt > 0);

    Collection<CurrencyPairMetaData> pairs = exchange.getExchangeMetaData().getCurrencyPairs().values();
    List<CurrencyPairMetaData> margins = pairs.stream()
            .filter( at -> at.isMarginOrderEnabled())
            .collect(Collectors.toList());
    Assert.assertTrue(margins.size() < pairs.size() && margins.size() > 0);
    System.out.println(pairs.size() + " pairs");
    System.out.println(margins.size() + " margin pairs");
  }

  public void placeLimitOrders() throws Exception {
    if (!API_KEY.isEmpty() && !SECRET_KEY.isEmpty() && !PASSPHRASE.isEmpty()) {
      final OkexTradeService okexTradeService = (OkexTradeService) exchange.getTradeService();

      // Place a sell order
      LimitOrder limitOrder = new LimitOrder.Builder(Order.OrderType.ASK, pair)
              .originalAmount(new BigDecimal("0.01"))
              .limitPrice(new BigDecimal("180"))
              .userReference(clientOrderId)
              .build();

      System.out.println("Placing order: " + limitOrder.toString());
      String orderId = okexTradeService.placeLimitOrder(limitOrder);
      System.out.println("Placed orderId: " + orderId);

      // Get Order Detail
      Order placedOrder = okexTradeService.getOrder(
              new DefaultQueryOrderParams(pair, null, clientOrderId, true));
      System.out.println("Placed Order Info: " + placedOrder.toString());

      LimitOrder updatedLimitOrder = new LimitOrder.Builder(Order.OrderType.ASK, pair)
              .originalAmount(new BigDecimal("0.015"))
              .limitPrice(new BigDecimal("185"))
              .userReference(clientOrderId)
              .build();

      System.out.println("Updating order: " + updatedLimitOrder.toString());
      String orderId2 = okexTradeService.changeOrder(updatedLimitOrder);
      System.out.println("Updated orderId: " + orderId2);

      // Get Updated Order Detail
      Order updatedOrder = okexTradeService.getOrder(
              new DefaultQueryOrderParams(pair, orderId2, clientOrderId, true));
      System.out.println("Updated Order Info: " + updatedOrder.toString());

      // Cancel that order
      boolean result = okexTradeService.cancelOrder(
              new DefaultCancelOrderParams(pair, null, clientOrderId, true));
      System.out.println("Cancellation result: " + result);
    }
  }

  public void placeMarketOrders() throws Exception {
    final OkexTradeService okexTradeService = (OkexTradeService) exchange.getTradeService();

    // Place a sell order
    MarketOrder sellMarginOrder = new MarketOrder.Builder(Order.OrderType.ASK, pair)
            .originalAmount(new BigDecimal("0.01"))
            .userReference(clientOrderId)
            .build();

    System.out.println("Placing sell order: " + sellMarginOrder.toString());
    String orderId = okexTradeService.placeMarketOrder(sellMarginOrder);
    System.out.println("Placed sell orderId: " + orderId);

    // Place a buy order
    MarketOrder buySpotOrder = new MarketOrder.Builder(Order.OrderType.BID, pair)
            .originalAmount(new BigDecimal("0.01"))
            .userReference(clientOrderId)
            .build();

    System.out.println("Placing buy order: " + buySpotOrder.toString());
    String buyOrderId = okexTradeService.placeMarketOrder(buySpotOrder);
    System.out.println("Placed buy orderId: " + buyOrderId);
  }
}
