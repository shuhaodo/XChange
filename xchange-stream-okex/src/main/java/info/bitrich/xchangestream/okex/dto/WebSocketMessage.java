package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class WebSocketMessage {
  private String op;
  private List<Map<String, String>> args;

  public WebSocketMessage(@JsonProperty("op") String op, @JsonProperty("args") List<Map<String, String>> args) {
    this.op = op;
    this.args = args;
  }
}
