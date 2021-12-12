package info.bitrich.xchangestream.okex;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.okex.dto.OkExOrderbookStreamStream;
import info.bitrich.xchangestream.okex.dto.OkExStreamOrderbookResponse;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.okex.v5.dto.marketdata.OkexOrderbook;

import java.util.*;

//public class OkExStreamingTradeDataService implements StreamingTradeService {
//    private final OkExStreamingService service;
//
//    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
//    private final Map<String, OkExOrderbookStreamStream> orderbooks = new HashMap<>();
//    private final String KEY_INST_ID = "instId";
//    private final String KEY_CHANNEL = "channel";
//    private final String BOOK_CHANNEL = "books50-l2-tbt";
//
//  OkExStreamingTradeDataService(OkExStreamingService service) {
//    this.service = service;
//  }
//
//  @Override
//    public
//  }
//}
