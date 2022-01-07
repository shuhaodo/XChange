package info.bitrich.xchangestream.okex;

import info.bitrich.xchangestream.core.*;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel.State;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.okex.v5.OkexExchange;

import java.util.ArrayList;
import java.util.List;

public class OkExStreamingExchange extends OkexExchange implements StreamingExchange {
  private static final String PUBLIC_API_URI = "wss://wsaws.okex.com:8443/ws/v5/public";
  private static final String PRIVATE_API_URI = "wss://wsaws.okex.com:8443/ws/v5/private";

  private final OkExStreamingService publicStreamingService;
  private final OkExStreamingService privateStreamingService;
  private OkExStreamingMarketDataService streamingMarketDataService;
  private OkExStreamingAccountService streamingAccountService;
  private OkExStreamingTradeService streamingTradeService;

  public OkExStreamingExchange() {
    publicStreamingService = new OkExStreamingService(PUBLIC_API_URI);
    privateStreamingService = new OkExStreamingService(PRIVATE_API_URI);
  }

  @Override
  public boolean supportConcurrentSubscriptions() {
    return true;
  }

  @Override
  protected void initServices() {
    super.initServices();
    privateStreamingService.setAuthData(
            exchangeSpecification.getApiKey(),
            exchangeSpecification.getSecretKey(),
            (String)exchangeSpecification.getExchangeSpecificParametersItem("passphrase")
    );
    streamingMarketDataService = new OkExStreamingMarketDataService(publicStreamingService);
    streamingAccountService = new OkExStreamingAccountService(privateStreamingService);
    streamingTradeService = new OkExStreamingTradeService(privateStreamingService);
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    List<Completable> completables = new ArrayList<>();
    completables.add(publicStreamingService.connect());
    completables.add(privateStreamingService.connect());

    return Completable.concat(completables);
  }

  @Override
  public Completable disconnect() {
    List<Completable> completables = new ArrayList<>();
    completables.add(publicStreamingService.disconnect());
    completables.add(privateStreamingService.disconnect());
    return Completable.concat(completables);
  }

  @Override
  public boolean isAlive() {
    boolean isPublicSocketOpen = publicStreamingService.isSocketOpen();
    boolean isPrivateSocketOpen = privateStreamingService.isSocketOpen();

    return isPublicSocketOpen && isPrivateSocketOpen;
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    List<Observable<Throwable>> observables = new ArrayList<>();
    observables.add(publicStreamingService.subscribeReconnectFailure());
    observables.add(privateStreamingService.subscribeReconnectFailure());

    return Observable.concat(observables);
  }

  @Override
  public Observable<Object> connectionSuccess() {
    List<Observable<Object>> observables = new ArrayList<>();
    observables.add(publicStreamingService.subscribeConnectionSuccess());
    observables.add(privateStreamingService.subscribeConnectionSuccess());

    return Observable.concat(observables);
  }

  @Override
  public Observable<State> connectionStateObservable() {
    List<Observable<State>> observables = new ArrayList<>();
    observables.add(publicStreamingService.subscribeConnectionState());
    observables.add(privateStreamingService.subscribeConnectionState());

    return Observable.concat(observables);
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public StreamingAccountService getStreamingAccountService() { return streamingAccountService; }

  @Override
  public StreamingTradeService getStreamingTradeService() { return streamingTradeService; }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    publicStreamingService.useCompressedMessages(compressedMessages);
    privateStreamingService.useCompressedMessages(compressedMessages);
  }
}
