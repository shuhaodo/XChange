package org.knowm.xchange.okex.v5;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.okex.v5.service.OkexMarketDataService;

@Slf4j
public class OkexExchangeIntegration {
  // Enter your authentication details here to run private endpoint tests
  private static final String API_KEY = System.getenv("api-key");
  private static final String SECRET_KEY = System.getenv("kex");
  private static final String PASSPHRASE = System.getenv("xek");

  protected static OkexExchange exchange;

  @BeforeClass
  public static void beforeClass() throws Exception {
    exchange = ExchangeFactory.INSTANCE.createExchange(OkexExchange.class);
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();
    spec.setApiKey(API_KEY);
    spec.setSecretKey(SECRET_KEY);
    spec.setExchangeSpecificParametersItem("passphrase", PASSPHRASE);
    exchange.applySpecification(spec);
  }

  @Test
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

  @Test
  public void testCreateExchangeShouldApplyDefaultSpecification() {
    ExchangeSpecification spec = new OkexExchange().getDefaultExchangeSpecification();
    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(spec);

    assertThat(exchange.getExchangeSpecification().getSslUri()).isEqualTo("https://www.okex.com");
    assertThat(exchange.getExchangeSpecification().getHost()).isEqualTo("okex.com");
    assertThat(exchange.getExchangeSpecification().getResilience().isRateLimiterEnabled())
        .isEqualTo(false);
    assertThat(exchange.getExchangeSpecification().getResilience().isRetryEnabled())
        .isEqualTo(false);
  }

  @Test
  public void testCreateExchangeShouldApplyResilience() {
    ExchangeSpecification spec = new OkexExchange().getDefaultExchangeSpecification();
    ExchangeSpecification.ResilienceSpecification resilienceSpecification =
        new ExchangeSpecification.ResilienceSpecification();
    resilienceSpecification.setRateLimiterEnabled(true);
    resilienceSpecification.setRetryEnabled(true);
    spec.setResilience(resilienceSpecification);

    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(spec);

    assertThat(exchange.getExchangeSpecification().getResilience().isRateLimiterEnabled())
        .isEqualTo(true);
    assertThat(exchange.getExchangeSpecification().getResilience().isRetryEnabled())
        .isEqualTo(true);
  }
/*
  @Test
  public void testOrderActions() throws Exception {
    if (!API_KEY.isEmpty() && !SECRET_KEY.isEmpty() && !PASSPHRASE.isEmpty()) {
      final OkexTradeService okexTradeService = (OkexTradeService) exchange.getTradeService();

      assertThat(exchange.getExchangeSpecification().getSslUri()).isEqualTo("https://www.okex.com");
      assertThat(exchange.getExchangeSpecification().getHost()).isEqualTo("okex.com");

      // Place a single order
      LimitOrder limitOrder =
          new LimitOrder(
              Order.OrderType.ASK, BigDecimal.TEN, TRX_USDT, null, new Date(), new BigDecimal(100));

      String orderId = okexTradeService.placeLimitOrder(limitOrder);
      log.info("Placed orderId: {}", orderId);

      // Amend the above order
      LimitOrder limitOrder2 =
          new LimitOrder(
              Order.OrderType.ASK,
              BigDecimal.TEN,
              TRX_USDT,
              orderId,
              new Date(),
              new BigDecimal(1000));
      String orderId2 = okexTradeService.changeOrder(limitOrder2);
      log.info("Amended orderId: {}", orderId2);

      // Get non-existent Order Detail
      Order failOrder =
          okexTradeService.getOrder(new DefaultQueryOrderParamInstrument(TRX_USDT, "2132465465"));
      log.info("Null Order: {}", failOrder);

      // Get Order Detail
      Order amendedOrder =
          okexTradeService.getOrder(new DefaultQueryOrderParamInstrument(TRX_USDT, orderId2));
      log.info("Amended Order: {}", amendedOrder);

      // Cancel that order
      boolean result =
          exchange
              .getTradeService()
              .cancelOrder(new OkexTradeParams.OkexCancelOrderParams(TRX_USDT, orderId2));
      log.info("Cancellation result: {}", result);

      // Place batch orders
      List<String> orderIds =
          okexTradeService.placeLimitOrder(Arrays.asList(limitOrder, limitOrder, limitOrder));
      log.info("Placed batch orderIds: {}", orderIds);

      // Amend batch orders
      List<LimitOrder> amendOrders = new ArrayList<>();
      for (String id : orderIds) {
        amendOrders.add(
            new LimitOrder(
                Order.OrderType.ASK,
                BigDecimal.TEN,
                TRX_USDT,
                id,
                new Date(),
                new BigDecimal(1000)));
      }
      List<String> amendedOrderIds = okexTradeService.changeOrder(amendOrders);
      log.info("Amended batch orderIds: {}", amendedOrderIds);

      OpenOrders openOrders = okexTradeService.getOpenOrders();
      log.info("Open Orders: {}", openOrders);

      // Cancel batch orders
      List<CancelOrderParams> cancelOrderParams = new ArrayList<>();
      for (String id : orderIds) {
        cancelOrderParams.add(new OkexTradeParams.OkexCancelOrderParams(TRX_USDT, id));
      }
      List<Boolean> results = okexTradeService.cancelOrder(cancelOrderParams);
      log.info("Cancelled order results: {}", results);
    }
  }

 */
}
