package com.ccc.ari.aggregation.application.service;

import com.ccc.ari.aggregation.domain.client.BlockChainClient;
import com.ccc.ari.aggregation.domain.client.IpfsClient;
import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.ccc.ari.global.contract.StreamingAggregationContract;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StreamingLogQueryService {

    private final BlockChainClient blockChainClient;
    private final IpfsClient ipfsClient;
    private final Web3j web3j;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${STREAMING_AGGREGATION_CONTRACT_ADDRESS}")
    private String contractAddress;

    public StreamingLogQueryService(BlockChainClient blockChainClient, IpfsClient ipfsClient,
                                    @Value("${AMOY_NODE_ENDPOINT}") String blockchainApiUrl) {
        this.blockChainClient = blockChainClient;
        this.ipfsClient = ipfsClient;
        this.web3j = Web3j.build(new HttpService(blockchainApiUrl));
    }

    /**
     * 주어진 trackId에 해당하는 모든 StreamingLog를 조회합니다.
     *
     * 1. 스마트 컨트랙트 이벤트 로그(RawAllTracksUpdated)를 EthFilter를 통해 조회하여,
     *    각 이벤트에 포함된 CID 목록을 수집합니다.
     * 2. 각 CID에 대해 IpfsClient.fetchData(cid)를 호출하여 IPFS에 저장된 JSON 데이터를 가져옵니다.
     * 3. JSON 데이터를 StreamingLog VO의 리스트로 파싱하고, 모든 결과를 하나의 리스트로 병합합니다.
     * 4. 병합된 리스트에서 trackId가 일치하는 StreamingLog만 필터링하여 반환합니다.
     *
     * @param trackId 조회할 트랙의 ID
     * @return 해당 trackId에 해당하는 StreamingLog 리스트
     */
    public List<StreamingLog> getStreamingLogsByTrackId(Integer trackId) {
        // 1. EthFilter 생성: 계약 주소와 블록 범위(Earliest ~ Latest) 설정
        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                contractAddress
        );
        // 이벤트 토픽 추가: RawAllTracksUpdated 이벤트의 토픽을 추가
        String eventTopic = StreamingAggregationContract.EventEncoder.encode(
                StreamingAggregationContract.RAWALLTRACKSUPDATED_EVENT);
        filter.addSingleTopic(eventTopic);

        // 2. Flowable API를 사용해 이벤트 로그 구독 및 수집
        List<StreamingAggregationContract.RawAllTracksUpdatedEventResponse> eventResponses =
                Flowable.fromPublisher(web3j.ethLogFlowable(filter))
                        .map(StreamingAggregationContract::getRawAllTracksUpdatedEventFromLog)
                        .toList()
                        .blockingGet();

        // 3. 모든 이벤트에서 CID 추출 후, IPFS에서 데이터 조회 및 파싱
        List<StreamingLog> allStreamingLogs = new ArrayList<>();
        for (StreamingAggregationContract.RawAllTracksUpdatedEventResponse event : eventResponses) {
            String cid = event.cid;
            // IPFS에서 데이터를 가져옵니다. (fetchData 메서드 구현 필요)
            String jsonData = ipfsClient.fetchData(cid);
            try {
                List<StreamingLog> logsForCid = objectMapper.readValue(
                        jsonData, new TypeReference<List<StreamingLog>>() {});
                allStreamingLogs.addAll(logsForCid);
            } catch (Exception e) {
                throw new RuntimeException("IPFS 데이터 파싱 실패 (CID: " + cid + ")", e);
            }
        }

        // 4. trackId로 필터링하여 반환
        return allStreamingLogs.stream()
                .filter(log -> log.getTrackId().equals(trackId))
                .collect(Collectors.toList());
    }

    // TODO: Flowable API를 사용해 이벤트 로그 조회
    // TODO: 이후에는 The Graph로 마이그레이션
}
