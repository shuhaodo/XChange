package info.bitrich.xchangestream.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Observable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceUserDataStreamingService extends JsonNettyStreamingService {

  private static final Logger LOG = LoggerFactory.getLogger(BinanceUserDataStreamingService.class);

  private static final String USER_API_BASE_URI = "wss://stream.binance.com:9443/stream?streams=";
  private String spotListenKey = "";
  private String marginListenKey = "";

  public static BinanceUserDataStreamingService create(String[] listenKeys) {
    String spotListenKey = listenKeys[0];
    String marginListenKey = listenKeys[1];
    String url = USER_API_BASE_URI + spotListenKey + "/" + marginListenKey;
    return new BinanceUserDataStreamingService(spotListenKey, marginListenKey, url);
  }

  private BinanceUserDataStreamingService(String spotListenKey, String marginListenKey, String url) {
    super(url, Integer.MAX_VALUE);
    this.spotListenKey = spotListenKey;
    this.marginListenKey = marginListenKey;
  }

  public Observable<JsonNode> subscribeChannel(BinanceWebSocketTypes eventType) {
    return super.subscribeChannel(eventType.getSerializedValue());
  }

  @Override
  public void messageHandler(String message) {
    LOG.debug("Received message: {}", message);
    super.messageHandler(message);
  }

  @Override
  protected void handleMessage(JsonNode message) {
    try {
      JsonNode data = message.get("data");
      if (data != null) {
        if (message.get("stream") != null) {
          String stream = message.get("stream").asText();
          ObjectNode n = (ObjectNode) data;
          n.put("wallet", stream.equals(marginListenKey) ? "MARGIN" : "SPOT");
          super.handleMessage(n);
        } else {
          super.handleMessage(data);
        }
      } else {
        super.handleMessage(message);
      }
    } catch (Exception e) {
      LOG.error("Error handling message: " + message, e);
    }
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    if (message.get("e") != null) {
      return message.get("e").asText();
    }

    JsonNode data = message.get("data");
    if (data != null && data.get("e") != null) {
      return data.get("e").asText();
    } else {
      return "";
    }
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    // No op. Disconnecting from the web socket will cancel subscriptions.
    return null;
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    // No op. Disconnecting from the web socket will cancel subscriptions.
    return null;
  }

  @Override
  protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
    return WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler.INSTANCE;
  }

  @Override
  public void sendMessage(String message) {
    // Subscriptions are made upon connection - no messages are sent.
  }
}
