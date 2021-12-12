package info.bitrich.xchangestream.okex.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkExStreamArgInstrument {
    @JsonProperty("channel")
    private String channel;

    @JsonProperty("instId")
    private String instrument;
}
