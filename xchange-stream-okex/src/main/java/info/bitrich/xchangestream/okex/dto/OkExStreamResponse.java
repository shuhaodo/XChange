package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OkExStreamResponse {
    @JsonProperty("action")
    private String action;

    @JsonProperty("arg")
    private OkExStreamArg arg;

    @JsonProperty("args")
    private List<OkExStreamArg> args;
}
