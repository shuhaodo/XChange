package org.knowm.xchange.binance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.account.*;
import org.knowm.xchange.binance.dto.trade.*;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface BinanceAuthenticated extends Binance {

  String SIGNATURE = "signature";
  String X_MBX_APIKEY = "X-MBX-APIKEY";

  @POST
  @Path("sapi/v1/margin/order")
  BinanceNewOrder newMarginOrder(
          @FormParam("symbol") String symbol,
          @FormParam("side") OrderSide side,
          @FormParam("type") OrderType type,
          @FormParam("timeInForce") TimeInForce timeInForce,
          @FormParam("quantity") BigDecimal quantity,
          @FormParam("price") BigDecimal price,
          @FormParam("newClientOrderId") String newClientOrderId,
          @FormParam("stopPrice") BigDecimal stopPrice,
          @FormParam("icebergQty") BigDecimal icebergQty,
          @FormParam("newOrderRespType") BinanceNewOrder.NewOrderResponseType newOrderRespType,
          @FormParam("sideEffectType") OrderSideEffect sideEffectType,
          @FormParam("recvWindow") Long recvWindow,
          @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam(SIGNATURE) ParamsDigest signature)
          throws IOException, BinanceException;

  @DELETE
  @Path("sapi/v1/margin/order")
  BinanceCancelledOrder cancelMarginOrder(
          @QueryParam("symbol") String symbol,
          @QueryParam("orderId") String orderId,
          @QueryParam("origClientOrderId") String origClientOrderId,
          @QueryParam("newClientOrderId") String newClientOrderId,
          @QueryParam("recvWindow") Long recvWindow,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam(SIGNATURE) ParamsDigest signature)
          throws IOException, BinanceException;

  @GET
  @Path("sapi/v1/margin/openOrders")
  List<BinanceOrder> openMarginOrders(
          @QueryParam("symbol") String symbol,
          @QueryParam("recvWindow") Long recvWindow,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam(SIGNATURE) ParamsDigest signature)
          throws IOException, BinanceException;

  @GET
  @Path("sapi/v1/margin/order")
  BinanceOrder marginOrderStatus(
          @QueryParam("symbol") String symbol,
          @QueryParam("orderId") long orderId,
          @QueryParam("origClientOrderId") String origClientOrderId,
          @QueryParam("recvWindow") Long recvWindow,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam(SIGNATURE) ParamsDigest signature)
          throws IOException, BinanceException;

  /**
   * Send in a new order
   *
   * @param symbol
   * @param side
   * @param type
   * @param timeInForce
   * @param quantity
   * @param price optional, must be provided for limit orders only
   * @param newClientOrderId optional, a unique id for the order. Automatically generated if not
   *     sent.
   * @param stopPrice optional, used with stop orders
   * @param icebergQty optional, used with iceberg orders
   * @param newOrderRespType optional, MARKET and LIMIT order types default to FULL, all other
   *     orders default to ACK
   * @param recvWindow optional
   * @param timestamp
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @POST
  @Path("api/v3/order")
  BinanceNewOrder newOrder(
      @FormParam("symbol") String symbol,
      @FormParam("side") OrderSide side,
      @FormParam("type") OrderType type,
      @FormParam("timeInForce") TimeInForce timeInForce,
      @FormParam("quantity") BigDecimal quantity,
      @FormParam("price") BigDecimal price,
      @FormParam("newClientOrderId") String newClientOrderId,
      @FormParam("stopPrice") BigDecimal stopPrice,
      @FormParam("icebergQty") BigDecimal icebergQty,
      @FormParam("newOrderRespType") BinanceNewOrder.NewOrderResponseType newOrderRespType,
      @FormParam("recvWindow") Long recvWindow,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Test new order creation and signature/recvWindow long. Creates and validates a new order but
   * does not send it into the matching engine.
   *
   * @param symbol
   * @param side
   * @param type
   * @param timeInForce
   * @param quantity
   * @param price
   * @param newClientOrderId optional, a unique id for the order. Automatically generated by
   *     default.
   * @param stopPrice optional, used with STOP orders
   * @param icebergQty optional used with icebergOrders
   * @param recvWindow optional
   * @param timestamp
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @POST
  @Path("api/v3/order/test")
  Object testNewOrder(
      @FormParam("symbol") String symbol,
      @FormParam("side") OrderSide side,
      @FormParam("type") OrderType type,
      @FormParam("timeInForce") TimeInForce timeInForce,
      @FormParam("quantity") BigDecimal quantity,
      @FormParam("price") BigDecimal price,
      @FormParam("newClientOrderId") String newClientOrderId,
      @FormParam("stopPrice") BigDecimal stopPrice,
      @FormParam("icebergQty") BigDecimal icebergQty,
      @FormParam("recvWindow") Long recvWindow,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Check an order's status.<br>
   * Either orderId or origClientOrderId must be sent.
   *
   * @param symbol
   * @param orderId optional
   * @param origClientOrderId optional
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/order")
  BinanceOrder orderStatus(
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") long orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Cancel an active order.
   *
   * @param symbol
   * @param orderId optional
   * @param origClientOrderId optional
   * @param newClientOrderId optional, used to uniquely identify this cancel. Automatically
   *     generated by default.
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @DELETE
  @Path("api/v3/order")
  BinanceCancelledOrder cancelOrder(
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") String orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("newClientOrderId") String newClientOrderId,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Cancels all active orders on a symbol. This includes OCO orders.
   *
   * @param symbol
   * @param recvWindow optional
   * @param timestamp
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @DELETE
  @Path("api/v3/openOrders")
  List<BinanceCancelledOrder> cancelAllOpenOrders(
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Get open orders on a symbol.
   *
   * @param symbol optional
   * @param recvWindow optional
   * @param timestamp
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/openOrders")
  List<BinanceOrder> openOrders(
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Get all account orders; active, canceled, or filled. <br>
   * If orderId is set, it will get orders >= that orderId. Otherwise most recent orders are
   * returned.
   *
   * @param symbol
   * @param orderId optional
   * @param limit optional
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/allOrders")
  List<BinanceOrder> allOrders(
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Get current account information.
   *
   * @param recvWindow optional
   * @param timestamp
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/account")
  BinanceAccountInformation account(
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  @GET
  @Path("sapi/v1/margin/account")
  BinanceMarginAccountInformation marginAccount(
          @QueryParam("recvWindow") Long recvWindow,
          @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
          @HeaderParam(X_MBX_APIKEY) String apiKey,
          @QueryParam(SIGNATURE) ParamsDigest signature) throws IOException, BinanceException;

  /**
   * Get trades for a specific account and symbol.
   *
   * @param symbol
   * @param orderId optional
   * @param startTime optional
   * @param endTime optional
   * @param fromId optional, tradeId to fetch from. Default gets most recent trades.
   * @param limit optional, default 500; max 1000.
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("api/v3/myTrades")
  List<BinanceTrade> myTrades(
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("fromId") Long fromId,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Submit a withdraw request.
   *
   * @param coin
   * @param address
   * @param addressTag optional for Ripple
   * @param amount
   * @param name optional, description of the address
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @POST
  @Path("/sapi/v1/capital/withdraw/apply")
  WithdrawResponse withdraw(
      @FormParam("coin") String coin,
      @FormParam("address") String address,
      @FormParam("addressTag") String addressTag,
      @FormParam("amount") BigDecimal amount,
      @FormParam("name") String name,
      @FormParam("recvWindow") Long recvWindow,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Fetch deposit history.
   *
   * @param coin optional
   * @param startTime optional
   * @param endTime optional
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("/sapi/v1/capital/deposit/hisrec")
  List<BinanceDeposit> depositHistory(
      @QueryParam("coin") String coin,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Fetch withdraw history.
   *
   * @param coin optional
   * @param startTime optional
   * @param endTime optional
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("/sapi/v1/capital/withdraw/history")
  List<BinanceWithdraw> withdrawHistory(
      @QueryParam("coin") String coin,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Fetch small amounts of assets exchanged BNB records.
   *
   * @param asset optional
   * @param startTime optional
   * @param endTime optional
   * @param recvWindow optional
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("/sapi/v1/asset/assetDividend")
  AssetDividendResponse assetDividend(
      @QueryParam("asset") String asset,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  @GET
  @Path("/sapi/v1/sub-account/sub/transfer/history")
  List<TransferHistory> transferHistory(
      @QueryParam("fromEmail") String fromEmail,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("page") Integer page,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  @GET
  @Path("/sapi/v1/sub-account/transfer/subUserHistory")
  List<TransferSubUserHistory> transferSubUserHistory(
      @QueryParam("asset") String asset,
      @QueryParam("type") Integer type,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Fetch deposit address.
   *
   * @param coin
   * @param recvWindow
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("/sapi/v1/capital/deposit/address")
  DepositAddress depositAddress(
      @QueryParam("coin") String coin,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Fetch asset details.
   *
   * @param recvWindow
   * @param timestamp
   * @param apiKey
   * @param signature
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  @GET
  @Path("/sapi/v1/asset/assetDetail")
  Map<String, AssetDetail> assetDetail(
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_MBX_APIKEY) String apiKey,
      @QueryParam(SIGNATURE) ParamsDigest signature)
      throws IOException, BinanceException;

  /**
   * Returns a listen key for websocket login.
   *
   * @param apiKey the api key
   * @return
   * @throws BinanceException
   * @throws IOException
   */
  @POST
  @Path("/api/v3/userDataStream")
  BinanceListenKey startUserDataStream(@HeaderParam(X_MBX_APIKEY) String apiKey)
      throws IOException, BinanceException;

  /**
   * Keeps the authenticated websocket session alive.
   *
   * @param apiKey the api key
   * @param listenKey the api secret
   * @return
   * @throws BinanceException
   * @throws IOException
   */
  @PUT
  @Path("/api/v3/userDataStream?listenKey={listenKey}")
  Map<?, ?> keepAliveUserDataStream(
      @HeaderParam(X_MBX_APIKEY) String apiKey, @PathParam("listenKey") String listenKey)
      throws IOException, BinanceException;

  /**
   * Closes the websocket authenticated connection.
   *
   * @param apiKey the api key
   * @param listenKey the api secret
   * @return
   * @throws BinanceException
   * @throws IOException
   */
  @DELETE
  @Path("/api/v3/userDataStream?listenKey={listenKey}")
  Map<?, ?> closeUserDataStream(
      @HeaderParam(X_MBX_APIKEY) String apiKey, @PathParam("listenKey") String listenKey)
      throws IOException, BinanceException;

  @POST
  @Path("/sapi/v1/userDataStream")
  BinanceListenKey startMarginUserDataStream(@HeaderParam(X_MBX_APIKEY) String apiKey)
          throws IOException, BinanceException;

  @PUT
  @Path("/sapi/v1/userDataStream?listenKey={listenKey}")
  Map<?, ?> keepAliveMarginUserDataStream(
          @HeaderParam(X_MBX_APIKEY) String apiKey, @PathParam("listenKey") String listenKey)
          throws IOException, BinanceException;


  @DELETE
  @Path("/sapi/v1/userDataStream?listenKey={listenKey}")
  Map<?, ?> closeMarginUserDataStream(
          @HeaderParam(X_MBX_APIKEY) String apiKey, @PathParam("listenKey") String listenKey)
          throws IOException, BinanceException;
}
