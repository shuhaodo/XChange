package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class OkExAuthenticationMessage {
  public OkExAuthenticationMessage(List<OkExAuthenticationArg> args) {
    this.args = args;
  }

  @JsonProperty("op")
  private final String op = "login";

  @JsonProperty("args")
  private List<OkExAuthenticationArg> args;

  public static class OkExAuthenticationArg {
    public OkExAuthenticationArg(String key, String pass, String ts, String sign) {
      this.apiKey = key;
      this.passphrase = pass;
      this.timestamp = ts;
      this.sign = sign;
    }

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("passphrase")
    private String passphrase;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("sign")
    private String sign;
  }
}
