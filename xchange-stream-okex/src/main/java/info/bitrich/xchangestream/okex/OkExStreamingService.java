package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.core.StreamingAuthedService;
import info.bitrich.xchangestream.okex.dto.OkExAuthenticationMessage;
import info.bitrich.xchangestream.okex.dto.OkExAuthenticationMessage.OkExAuthenticationArg;
import info.bitrich.xchangestream.okex.dto.OkExStreamResponse;
import info.bitrich.xchangestream.okex.dto.WebSocketMessage;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.knowm.xchange.exceptions.ExchangeException;

public class OkExStreamingService extends StreamingAuthedService {

  private final Observable<Long> pingPongSrc = Observable.interval(15, 15, TimeUnit.SECONDS);

  private Disposable pingPongSubscription;

  public OkExStreamingService(String apiUrl) {
    super(apiUrl, null, null, null);
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
  protected String doGetSubscribeMessage(String channelName, Object... args) throws IOException {
    List<Map<String, String>> channels = new ArrayList<Map<String, String>>();
    if (args.length > 0) {
      channels = (List<Map<String, String>>)args[0];
    }
    return objectMapper.writeValueAsString(new WebSocketMessage("subscribe", channels));
  }

  @Override
  protected Object createAuthMessageObject() {
    String timestamp = System.currentTimeMillis() / 1000 + "";
    String signData = timestamp + "GET" + "/users/self/verify";
    String signature = getSignature(signData);
    List<OkExAuthenticationArg> args = new ArrayList<>();
    OkExAuthenticationArg arg = new OkExAuthenticationArg(apiKey, passPhrase, timestamp, signature);
    args.add(arg);
    return new OkExAuthenticationMessage(args);
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
  protected void onChannelInactive() {
    if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
      pingPongSubscription.dispose();
    }
    super.onChannelInactive();
  }
}
