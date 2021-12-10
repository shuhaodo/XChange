package info.bitrich.xchangestream.okex.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexOrderbook;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexPublicOrder;

import static org.knowm.xchange.okex.v5.OkexAdapters.adaptOrderbookOrder;

/** Created by Shuhao Zhang on 16.11.17. */
public class OkExOrderbookStreamStream {
  private final BigDecimal zero = new BigDecimal(0);
  private Instrument instrument;
  private final SortedMap<BigDecimal, OkexPublicOrder> asks;
  private final SortedMap<BigDecimal, OkexPublicOrder> bids;

  public OkExOrderbookStreamStream(Instrument instrument) {
    this.asks =
        new TreeMap<>(
            java.util.Collections
                .reverseOrder()); // Because okcoin adapter uses reverse sort for asks!!!
    this.bids = new TreeMap<>();
    this.instrument = instrument;
  }

  public OkExOrderbookStreamStream(OkexOrderbook book, Instrument instrument) {
    this(instrument);
    createFromDepth(book);
  }

  private void createFromDepth(OkexOrderbook book) {
    createFromDepthLevels(book.getAsks(), Order.OrderType.ASK);
    createFromDepthLevels(book.getBids(), Order.OrderType.BID);
  }

  private void createFromDepthLevels(List<OkexPublicOrder> orders, Order.OrderType side) {
    SortedMap<BigDecimal, OkexPublicOrder> orderbookLevels = side == Order.OrderType.ASK ? asks : bids;
    for (OkexPublicOrder order : orders) {
      orderbookLevels.put(order.getPrice(), order);
    }
  }

  public void updateLevels(OkexOrderbook book) {
    for (OkexPublicOrder order : book.getAsks()) {
      updateLevel(order, Order.OrderType.ASK);
    }
    for (OkexPublicOrder order : book.getAsks()) {
      updateLevel(order, Order.OrderType.BID);
    }
  }

  private void updateLevel(OkexPublicOrder order, Order.OrderType side) {
    SortedMap<BigDecimal, OkexPublicOrder> orderBookSide = side == Order.OrderType.ASK ? asks : bids;
    boolean shouldDelete = order.getVolume().compareTo(zero) == 0;
    BigDecimal price = order.getPrice();
    orderBookSide.remove(price);
    if (!shouldDelete) {
      orderBookSide.put(price, order);
    }
  }

  public OrderBook toOrderbook() {
    List<LimitOrder> asksOrders = new ArrayList<>();
    List<LimitOrder> bidsOrders = new ArrayList<>();
    asks.values().forEach(
            okexAsk ->
                    asksOrders.add(
                            adaptOrderbookOrder(
                                    okexAsk.getVolume(), okexAsk.getPrice(), instrument, Order.OrderType.ASK)));

    bids.values().forEach(
            okexBid ->
                    bidsOrders.add(
                            adaptOrderbookOrder(
                                    okexBid.getVolume(), okexBid.getPrice(), instrument, Order.OrderType.BID)));

    return new OrderBook(Date.from(Instant.now()), asksOrders, bidsOrders);
  }
}
