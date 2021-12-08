package org.knowm.xchange.binance;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.marketdata.*;
import org.knowm.xchange.binance.dto.meta.BinanceSystemStatus;
import org.knowm.xchange.binance.dto.meta.BinanceTime;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;
import static org.knowm.xchange.binance.BinanceAuthenticated.SIGNATURE;
import static org.knowm.xchange.binance.BinanceAuthenticated.X_MBX_APIKEY;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface Binance {

  /**
   * Fetch system status which is normal or system maintenance.
   *
   * @throws IOException
   */
  @GET
  @Path("sapi/v1/system/status")
  BinanceSystemStatus systemStatus() throws IOException;

  /**
   * Test connectivity to the Rest API.
   *
   * @return
   * @throws IOException
   */
  @GET
  @Path("api/v3/ping")
  Object ping() throws IOException;

  /**
   * Test connectivity to the Rest API and get the current server time.
   *
   * @return
   * @throws IOException
   */
  @GET
  @Path("api/v3/time")
  BinanceTime time() throws IOException;

  /**
   * Current exchange trading rules and symbol information.
   *
   * @return
   * @throws IOException
   */
  @GET
  @Path("api/v3/exchangeInfo")
  BinanceExchangeInfo exchangeInfo() throws IOException;

  /**
   * @param symbol
   * @param limit optional, default 100 max 5000. Valid limits: [5, 10, 20, 50, 100, 500, 1000,
   *     5000]
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/depth")
  BinanceOrderbook depth(@QueryParam("symbol") String symbol, @QueryParam("limit") Integer limit)
      throws IOException, BinanceException;

  /**
   * Get compressed, aggregate trades. Trades that fill at the time, from the same order, with the
   * same price will have the quantity aggregated.<br>
   * If both startTime and endTime are sent, limit should not be sent AND the distance between
   * startTime and endTime must be less than 24 hours.<br>
   * If frondId, startTime, and endTime are not sent, the most recent aggregate trades will be
   * returned.
   *
   * @param symbol
   * @param fromId optional, ID to get aggregate trades from INCLUSIVE.
   * @param startTime optional, Timestamp in ms to get aggregate trades from INCLUSIVE.
   * @param endTime optional, Timestamp in ms to get aggregate trades until INCLUSIVE.
   * @param limit optional, Default 500; max 500.
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/aggTrades")
  List<BinanceAggTrades> aggTrades(
      @QueryParam("symbol") String symbol,
      @QueryParam("fromId") Long fromId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("limit") Integer limit)
      throws IOException, BinanceException;

  /**
   * Kline/candlestick bars for a symbol. Klines are uniquely identified by their open time.<br>
   * If startTime and endTime are not sent, the most recent klines are returned.
   *
   * @param symbol
   * @param interval
   * @param limit optional, default 500; max 500.
   * @param startTime optional
   * @param endTime optional
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/klines")
  List<Object[]> klines(
      @QueryParam("symbol") String symbol,
      @QueryParam("interval") String interval,
      @QueryParam("limit") Integer limit,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime)
      throws IOException, BinanceException;

  /**
   * 24 hour price change statistics for all symbols. - bee carreful this api call have a big
   * weight, only about 4 call per minut can be without ban.
   *
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/ticker/24hr")
  List<BinanceTicker24h> ticker24h() throws IOException, BinanceException;

  /**
   * 24 hour price change statistics.
   *
   * @param symbol
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/ticker/24hr")
  BinanceTicker24h ticker24h(@QueryParam("symbol") String symbol)
      throws IOException, BinanceException;

  /**
   * Latest price for a symbol.
   *
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/ticker/price")
  BinancePrice tickerPrice(@QueryParam("symbol") String symbol)
      throws IOException, BinanceException;

  /**
   * Latest price for all symbols.
   *
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/ticker/price")
  List<BinancePrice> tickerAllPrices() throws IOException, BinanceException;

  /**
   * Best price/qty on the order book for all symbols.
   *
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/ticker/bookTicker")
  List<BinancePriceQuantity> tickerAllBookTickers() throws IOException, BinanceException;

  /**
   * @return list of pairs for margin trade
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("sapi/v1/margin/allPairs")
  List<BinanceMarginPair> marginAllPairs(
          @HeaderParam(X_MBX_APIKEY) String apiKey) throws IOException, BinanceException;

  /**
   * @return list of margin currencies
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("sapi/v1/margin/crossMarginData")
  List<BinanceMarginCurrency> marginCurrencies(
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @QueryParam(SIGNATURE) ParamsDigest signature) throws IOException, BinanceException;

  /**
   * @return list of margin currencies
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("sapi/v1/asset/assetDetail")
  Map<String, BinanceAsset> allCurrencies(
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @QueryParam(SIGNATURE) ParamsDigest signature) throws IOException, BinanceException;
}
