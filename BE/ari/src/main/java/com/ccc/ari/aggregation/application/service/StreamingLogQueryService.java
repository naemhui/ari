package com.ccc.ari.aggregation.application.service;

import com.ccc.ari.aggregation.application.repository.AggregatedDataRepository;
import com.ccc.ari.aggregation.domain.AggregatedData;
import com.ccc.ari.aggregation.domain.client.BlockChainClient;
import com.ccc.ari.aggregation.domain.client.IpfsClient;
import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.ccc.ari.aggregation.infrastructure.entity.AggregatedDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamingLogQueryService {

    //private final BlockChainClient blockChainClient;
    private final IpfsClient ipfsClient;
    //private final Web3j web3j;
    private final AggregatedDataRepository aggregatedDataRepository;

    public List<StreamingLog> findStreamingLogByTrackId(Integer trackId) {
        return aggregatedDataRepository.findAll().stream()
                // 1. AggregatedDataEntity에서 CID 추출
                .map(AggregatedDataEntity::getCid)
                // 2. IPFS에서 각 CID에 해당하는 데이터 조회
                .map(ipfsClient::get)
                // 3. JSON 응답을 AggregatedData 객체로 변환
                .map(AggregatedData::fromJson)
                // 4. 각 AggregatedData의 StreamingLogs를 하나의 스트림으로 평탄화
                .flatMap(aggregatedData -> aggregatedData.getStreamingLogs().stream())
                // 5. 특정 trackId와 일치하는 StreamingLog만 필터링
                .filter(streamingLog -> streamingLog.getTrackId().equals(trackId))
                // 6. 타임스탬프 기준으로 정렬
                .sorted(Comparator.comparing(StreamingLog::getTimestamp))
                // 7. 결과를 List로 수집
                .toList();
    }
}
