package com.ccc.ari.aggregation.domain.vo;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
public class StreamingLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 언제
    private final Instant timestamp; // UTC 기준 시간
    // 누가
    private final Integer memberId;
    private final String memberNickname;
    // 무엇을
    private final Integer trackId;
    private final String trackTitle;

    public StreamingLog(Instant timestamp, Integer memberId, String memberNickname, Integer trackId, String trackTitle) {
      this.timestamp = timestamp;
      this.memberId = memberId;
      this.memberNickname = memberNickname;
      this.trackId = trackId;
      this.trackTitle = trackTitle;
    }

    /**
     * UTC 기준의 시간을 한국 시간으로 변환 후 포맷팅하여 반환합니다.
     */
    public String timestampToString() {
        return timestamp
                .atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return timestamp.toString() + " " +
                memberNickname + "(id:" + memberId + ") " +
                trackTitle + "(id:" + trackId + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StreamingLog log) {
            return log.getTimestamp().equals(timestamp) &&
                    log.getMemberId().equals(memberId) &&
                    log.getMemberNickname().equals(memberNickname) &&
                    log.getTrackId().equals(trackId) &&
                    log.getTrackTitle().equals(trackTitle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, memberId, memberNickname, trackId, trackTitle);
    }
}
