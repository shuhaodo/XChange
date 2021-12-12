package info.bitrich.xchangestream.core;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.knowm.xchange.service.BaseParamsDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import java.io.IOException;
import java.util.Base64;

public abstract class StreamingAuthedService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(StreamingAuthedService.class);

    protected String apiKey;
    protected String apiSecret;
    protected String passPhrase;
    protected boolean isAuthed;

    public StreamingAuthedService(String apiUrl,
                                  String apiKey,
                                  String apiSecret,
                                  String passPhrase) {
        super(apiUrl);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.passPhrase = passPhrase;
    }

    public void setAuthData(String apiKey,
                            String apiSecret,
                            String passPhrase) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.passPhrase = passPhrase;
    }

    protected String getSignature(String data) {
        return BaseStreamDigest.createSignature(apiSecret, data);
    }

    abstract protected Object createAuthMessageObject();

    public void authenticate() {
        LOG.info("Authenticating...");
        sendObjectMessage(createAuthMessageObject());
        isAuthed = true;
        try {
            Thread.sleep(5000);
            LOG.info("Authenticated");
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        return null;
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        if (apiKey != null && apiSecret != null && !isAuthed) {
            authenticate();
        }
        return doGetSubscribeMessage(channelName, args);
    }

    @Override
    public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
        return null;
    }

    abstract protected String doGetSubscribeMessage(String channelName, Object... args) throws IOException;

    public static class BaseStreamDigest extends BaseParamsDigest {
        private BaseStreamDigest(String secretKeyBase64) {
            super(secretKeyBase64, HMAC_SHA_256);
        }

        public static String createSignature(String secretKeyBase64, String data) {
            if (secretKeyBase64 == null || data == null) return null;
            BaseStreamDigest digest = new BaseStreamDigest(secretKeyBase64);
            Mac mac = digest.getMac();
            mac.update(data.getBytes());

            return Base64.getEncoder().encodeToString(mac.doFinal());
        }

        @Override
        public String digestParams(RestInvocation restInvocation) {
            return null;
        }
    }
}
