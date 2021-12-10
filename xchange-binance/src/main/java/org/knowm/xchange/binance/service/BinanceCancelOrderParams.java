package org.knowm.xchange.binance.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderByUserReferenceParams;

public class BinanceCancelOrderParams implements CancelOrderByIdParams,
        CancelOrderByCurrencyPair, CancelOrderByUserReferenceParams {
  private final String orderId;
  private final CurrencyPair pair;
  private final String clientOrderId;

  public BinanceCancelOrderParams(CurrencyPair pair, String orderId) {
    this(pair, orderId, null);
  }

  public BinanceCancelOrderParams(CurrencyPair pair, String orderId, String clientOrderId) {
    this.pair = pair;
    this.orderId = orderId;
    this.clientOrderId = clientOrderId;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  @Override
  public String getOrderId() {
    return orderId;
  }

  @Override
  public String getUserReference() {
    return clientOrderId;
  }
}
