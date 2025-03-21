package com.ccc.ari.aggregation.domain.service;

import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.ccc.ari.global.event.StreamingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * StreamingLogCollectorService는 kakfa에서 전달된 StreamingEvent를
 * 도메인 VO인 StreamingLog로 변환하는 책임을 집니다.
 */
@Service
public class StreamingLogCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(StreamingLogCollectorService.class);

    /**
     * StreamingEvent를 받아 해당 정보를 StreamingLog VO로 생성합니다.
     *
     * @param event 수신된 스트리밍 이벤트
     * @return 변환된 StreamingLog 객체
     * @throws IllegalArgumentException 이벤트가 null인 경우
     */
    public StreamingLog createStreamingLog(StreamingEvent event) {
        if (event == null) {
            logger.error("수신된 StreamingEvent가 null입니다.");
            throw new IllegalArgumentException("StreamingEvent가 null입니다!!");
        }

        // TODO: 추후 인증 구현 시 필드 검증 로직을 추가하겠습니다.
        StreamingLog streamingLog = new StreamingLog(event.getTimestamp(),
                event.getMemberId(), event.getNickname(),
                event.getTrackId(), event.getTrackTitle());

        // 로그: 변환된 StreamingLog 정보 출력
        logger.info("StreamingLog 생성 완료: {}", streamingLog);

        return streamingLog;
    }
}
