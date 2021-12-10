package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.okex.dto.OkExStreamResponse;
import info.bitrich.xchangestream.okex.dto.WebSocketMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;
import org.knowm.xchange.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkExStreamingService extends JsonNettyStreamingService {

  private final Observable<Long> pingPongSrc = Observable.interval(15, 15, TimeUnit.SECONDS);

  private Disposable pingPongSubscription;

  public OkExStreamingService(String apiUrl) {
    super(apiUrl);
  }

  @Override
  public Completable connect() {
    Completable conn = super.connect();
    return conn.andThen(
        (CompletableSource)
            (completable) -> {
              try {
                if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
                  pingPongSubscription.dispose();
                }
                pingPongSubscription =
                    pingPongSrc.subscribe(o -> this.sendMessage("ping"));
                completable.onComplete();
              } catch (Exception e) {
                completable.onError(e);
              }
            });
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    OkExStreamResponse response = objectMapper.treeToValue(message, OkExStreamResponse.class);
    if (response != null && response.getArg() != null && response.getArg().getChannel() != null) {
      return response.getArg().getChannel();
    } else if (response != null && response.getArgs() != null && !response.getArgs().isEmpty()) {
      return response.getArgs().get(0).getChannel();
    }
    return "unknown";
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    List<Map<String, String>> channels = new ArrayList<Map<String, String>>();
    if (args.length > 0) {
      channels = (List<Map<String, String>>)args[0];
    }
    return objectMapper.writeValueAsString(new WebSocketMessage("subscribe", channels));
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(new WebSocketMessage("unsubscribe", new ArrayList<Map<String, String>>()));
  }

  @Override
  protected void handleMessage(JsonNode message) {
    if (message.asText() != null && message.asText().equals("pong")) {
      return; //ignore
    }

    if (message.get("event") != null) {
      String event = message.get("event").asText();
      if ("error".equals(event)) {
          super.handleError(
                  message,
                  new ExchangeException(
                          "Error code: " + message.get("code").asText()));
      }
      return; //ignore event messages
    }

    super.handleMessage(message);
  }

  @Override
  protected WebSocketClientHandler getWebSocketClientHandler(
      WebSocketClientHandshaker handshaker,
      WebSocketClientHandler.WebSocketMessageHandler handler) {
    return new OkCoinNettyWebSocketClientHandler(handshaker, handler);
  }

  protected class OkCoinNettyWebSocketClientHandler extends NettyWebSocketClientHandler {

    private final Logger LOG = LoggerFactory.getLogger(OkCoinNettyWebSocketClientHandler.class);

    protected OkCoinNettyWebSocketClientHandler(
            WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
      super(handshaker, handler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
        pingPongSubscription.dispose();
      }
      super.channelInactive(ctx);
    }
  }
}
