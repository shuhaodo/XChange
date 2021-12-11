package org.knowm.xchange.service.trade.params.orders;

import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.MarginOrderParam;

public class DefaultQueryOrderParams extends DefaultQueryOrderParam
    implements OrderQueryParamInstrument, OrderQueryParamClientId, MarginOrderParam {
  private Instrument instrument;
  private String clientId;
  private boolean isMarginOrder;

  public DefaultQueryOrderParams(Instrument instrument,
                                 String orderId,
                                 String clientId,
                                 boolean isMarginOrder) {
    super(orderId);
    this.instrument = instrument;
    this.clientId = clientId;
    this.isMarginOrder = isMarginOrder;
  }

  @Override
  public Instrument getInstrument() {
    return instrument;
  }

  @Override
  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  @Override
  public String getClientId() { return clientId; }

  @Override
  public void setClientId(String clientId) { this.clientId = clientId; }

  @Override
  public boolean getIsMarginOrder() { return isMarginOrder; }
}
