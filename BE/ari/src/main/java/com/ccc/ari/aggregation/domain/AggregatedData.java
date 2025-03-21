package com.ccc.ari.aggregation.domain;

import com.ccc.ari.aggregation.domain.vo.AggregationPeriod;
import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@JsonDeserialize(builder = AggregatedData.AggregatedDataBuilder.class)
public class AggregatedData {

    // 집계 기간 VO
    private final AggregationPeriod period;
    // 스트리밍 로그 VO List
    private final List<StreamingLog> streamingLogs;

    @JsonPOJOBuilder(withPrefix = "")  // Lombok의 기본 빌더 메서드 프리픽스 설정
    public static class AggregatedDataBuilder {
    }

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
        mapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
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
        mapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
        try {
            return mapper.readValue(jsonData, AggregatedData.class);
        } catch (Exception e) {
            throw new RuntimeException("AggregatedData 역직렬화 실패", e);
        }
    }
}