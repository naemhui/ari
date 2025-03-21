package com.ccc.ari.aggregation.domain;

import com.ccc.ari.aggregation.domain.vo.AggregationPeriod;
import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AggregatedData {

    // 집계 기간 VO
    private final AggregationPeriod period;
    // 스트리밍 로그 VO List
    private final List<StreamingLog> streamingLogs;

    public AggregatedData(AggregationPeriod period,
                          List<StreamingLog> streamingLogs) {
        this.period = period;
        this.streamingLogs = streamingLogs;
    }

    /**
     * AggregatedData 객체를 JSON 문자열로 직렬화합니다.
     */
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AggregatedData JSON 직렬화 실패", e);
        }
    }

    /**
     * JSON 문자열을 AggregatedData 객체로 역직렬화합니다.
     */
    public static AggregatedData fromJson(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonData, AggregatedData.class);
        } catch (Exception e) {
            throw new RuntimeException("AggregatedData 역직렬화 실패", e);
        }
    }
}