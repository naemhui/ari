package com.ccc.ari.aggregation.domain.service;

import com.ccc.ari.aggregation.application.repository.AggregatedDataRepository;
import com.ccc.ari.aggregation.domain.AggregatedData;
import com.ccc.ari.aggregation.domain.client.BlockChainClient;
import com.ccc.ari.aggregation.domain.client.IpfsClient;
import com.ccc.ari.aggregation.domain.client.IpfsResponse;
import com.ccc.ari.aggregation.infrastructure.entity.AggregatedDataEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AggregationOnChainService는 AggregatedData를 기반으로
 * IPFS에 스트리밍 집계 데이터를 저장하고,
 * 해당 데이터의 CID를 블록체인에 커밋하는 도메인 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AggregationOnChainService {

    private final IpfsClient ipfsClient;
    //private final BlockChainClient blockChainClient;
    private final AggregatedDataRepository aggregatedDataRepository;
    private static final Logger logger = LoggerFactory.getLogger(AggregationOnChainService.class);

    /**
     * AggregatedData를 직렬화하여 IPFS에 저장하고,
     * 반환된 CID를 블록체인에 커밋합니다.
     *
     * @param aggregatedData 집계 결과 Aggregate Root
     * @return 트랜잭션 해시
     */
    public void publishAggregatedData(AggregatedData aggregatedData) {
        // 1. AggregatedData를 직렬화
        String jsonData = aggregatedData.toJson();
        logger.info("AggregatedData JSON 직렬화: {}", jsonData);

        // 2. IPFS에 데이터를 저장하고, CID를 포함하는 IpfsResponse를 획득
        IpfsResponse ipfsResponse = ipfsClient.save(jsonData);
        logger.info("IPFS 데이터 저장 완료 CID: {}", ipfsResponse.getCid());

        // 3. 획득한 CID를 블록체인에 커밋 -> mvp 시연 버전: DB에 저장
//        String txHash = blockChainClient.commitRawAllTracks(ipfsResponse.getCid());
//        logger.info("CID, Merkle Root Blockchain에 커밋 완료 Transaction Hash: {}", txHash);
        AggregatedDataEntity aggregatedDataEntity = AggregatedDataEntity.builder()
                .cid(ipfsResponse.getCid())
                .build();
        aggregatedDataRepository.save(aggregatedDataEntity);
    }
}