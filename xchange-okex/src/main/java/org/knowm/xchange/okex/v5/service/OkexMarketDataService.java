package org.knowm.xchange.okex.v5.service;

import java.io.IOException;
import java.util.List;

import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.okex.v5.OkexAdapters;
import org.knowm.xchange.okex.v5.OkexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.Params;

/** Author: Max Gao (gaamox@tutanota.com) Created: 08-06-2021 */
public class OkexMarketDataService extends OkexMarketDataServiceRaw implements MarketDataService {
  public static final String SPOT = "SPOT";
  public static final String MARGIN = "MARGIN";
  public static final String SWAP = "SWAP";
  public static final String FUTURES = "FUTURES";
  public static final String OPTION = "OPTION";

  public OkexMarketDataService(OkexExchange exchange, ResilienceRegistries resilienceRegistries) {
    super(exchange, resilienceRegistries);
  }

  @Override
  public OrderBook getOrderBook(Instrument instrument, Object... args) throws IOException {
    return OkexAdapters.adaptOrderBook(
        getOkexOrderbook(OkexAdapters.adaptCurrencyPairId(instrument)), instrument);
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
    return this.getOrderBook((Instrument) currencyPair, args);
  }

  @Override
  public Trades getTrades(Instrument instrument, Object... args) throws IOException {
    return OkexAdapters.adaptTrades(
        getOkexTrades(OkexAdapters.adaptCurrencyPairId(instrument), 100).getData(), instrument);
  }

  @Override
  public List<Ticker> getTickers(Params params) throws IOException {
    return OkexAdapters.adaptTickers(getOkexTickers().getData());
  }
}
