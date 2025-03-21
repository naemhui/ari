package com.ccc.ari.aggregation.application.service;

import com.ccc.ari.aggregation.domain.AggregatedData;
import com.ccc.ari.aggregation.domain.service.AggregationOnChainService;
import com.ccc.ari.aggregation.event.AggregationCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregationOnChainApplicationService {

    private final AggregationOnChainService aggregationOnChainService;
    private final Logger logger = LoggerFactory.getLogger(AggregationOnChainApplicationService.class);

    /**
     * AggregationBatchService가 발행한 이벤트로부터
     * AggregatedData를 추출해 IPFS에 저장하고, 반환된 CID를 스마트 컨트랙트에 커밋합니다.
     *
     * @param event 집계 결과 Aggregate Root
     */
    @EventListener
    public void handleAggregationCompleted(AggregationCompletedEvent event) {
        logger.info("AggregationCompletedEvent 수신 완료: {}", event.getAggregatedData().toString());
        // 1. 이벤트 객체로부터 AggregatedData 추출
        AggregatedData aggregatedData = event.getAggregatedData();
        // 2. 도메인 서비스 aggregationOnChainService를 사용해 IPFS 저장 및 온체인 커밋 -> mvc 버전: DB 저장
        aggregationOnChainService.publishAggregatedData(aggregatedData);
        //logger.info("집계된 데이터가 블록체인에 게시되었습니다, 트랜잭션 해시: {}", txHash);
    }
}
