package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.okex.dto.OkExOrderbookStreamStream;
import info.bitrich.xchangestream.okex.dto.OkExStreamTradeResponse;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.okex.v5.OkexAdapters;
import org.knowm.xchange.okex.v5.dto.trade.OkexOrderDetails;

import java.util.*;
import java.util.stream.Collectors;

public class OkExStreamingTradeService implements StreamingTradeService {
    private final OkExStreamingService service;

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
    private final String KEY_CHANNEL = "channel";
    private final String CHANNEL_ORDERS = "orders";

  OkExStreamingTradeService(OkExStreamingService service) {
    this.service = service;
  }

  /**
   *
   * @return
   */
  @Override
  public Observable<List<Order>> getOrderChanges() {
    List<Map<String, String>> channels = new ArrayList<>();
      Map<String, String> map = new HashMap<>();
      map.put(KEY_CHANNEL, CHANNEL_ORDERS);
      map.put("instType", "SPOT");
      channels.add(map);
    return service
        .subscribeChannel(CHANNEL_ORDERS, channels)
        .map(
            s -> {
                try {
                    OkExStreamTradeResponse response = mapper.treeToValue(s, OkExStreamTradeResponse.class);
                    if (response == null || response.getData() == null) {
                        return new ArrayList<>();
                    }
                    List<OkexOrderDetails> orders = response.getData();
                    return orders.stream().map(o -> OkexAdapters.adaptOrder(o))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            });
  }
}
