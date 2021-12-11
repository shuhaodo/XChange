package org.knowm.xchange.service.trade.params;

import org.knowm.xchange.instrument.Instrument;

public class DefaultCancelOrderParams
    implements CancelOrderByIdParams,
        CancelOrderByInstrument,
        CancelOrderByUserReferenceParams,
        MarginOrderParam {

  private Instrument instrument;
  private String orderId;
  private String userReference;
  private boolean isMarginOrder;

  public DefaultCancelOrderParams(
          Instrument instrument,
          String orderId,
          String userReference,
          boolean isMarginOrder) {
    this.instrument = instrument;
    this.orderId = orderId;
    this.userReference = userReference;
    this.isMarginOrder = isMarginOrder;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setUserReference(String userReference) { this.userReference = userReference; }

  @Override
  public Instrument getInstrument() {
    return instrument;
  }

  @Override
  public String getOrderId() {
    return orderId;
  }

  @Override
  public String getUserReference() { return userReference; }

  @Override
  public boolean getIsMarginOrder() { return isMarginOrder; }
}
