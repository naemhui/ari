package com.ccc.ari.aggregation.ui.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StreamingLogResponse {

    private String nickname;
    private String datetime;
}
