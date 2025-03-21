package com.ccc.ari.aggregation.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Builder
@Getter
@JsonDeserialize(builder = AggregationPeriod.AggregationPeriodBuilder.class)
public class AggregationPeriod {

    private final Instant start;
    private final Instant end;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AggregationPeriodBuilder {
        // builder 구현
    }

    public AggregationPeriod(Instant start, Instant end) {
        if(end.isBefore(start)) {
            throw new IllegalArgumentException("끝 시간은 반드시 시작 시간보다 이후이어야 합니다!!");
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "집계 기간: " +
                start + " ~ " + end;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AggregationPeriod period) {
            return period.getStart().equals(start) && period.getEnd().equals(end);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
