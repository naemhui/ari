package com.ccc.ari.aggregation.infrastructure.adapter;

import com.ccc.ari.aggregation.domain.client.IpfsClient;
import com.ccc.ari.aggregation.domain.client.IpfsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class IpfsClientImpl implements IpfsClient {

    private final String ipfsApiUrl;
    private final String jwtToken;
    private final String gatewayDomain;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(IpfsClientImpl.class);

    public IpfsClientImpl(@Value("${PINATA_ENDPOINT}") String ipfsApiUrl,
                          @Value("${PINATA_JWT}") String jwtToken,
                          @Value("${PINATA_GATEWAY}") String gatewayDomain) {
        this.restTemplate = new RestTemplate();
        this.ipfsApiUrl = ipfsApiUrl;
        this.jwtToken = jwtToken;
        this.gatewayDomain = gatewayDomain;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 데이터를 IPFS에 저장하고 CID를 반환합니다.
     *
     * @param data 직렬화된 문자열 데이터
     * @return IpfsResponse 객체 (CID 값을 포함)
     */
    public IpfsResponse save(String data) {
        log.info("IPFS에 데이터를 저장 중입니다...");

        // 1. multipart/form-data 요청 본문 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "data.json";
            }
        };
        body.add("file", resource);

        // Pinata V3 API의 경우 network 파라미터 추가
        body.add("network", "public");  // public 네트워크 선택

        // 2. multipart/form-data용 헤더 설정 및 JWT Bearer 인증 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 3. Pinata V3 API 엔드포인트로 POST 요청 전송
        String pinataV3Endpoint = "https://uploads.pinata.cloud/v3/files";
        ResponseEntity<String> response = restTemplate.postForEntity(pinataV3Endpoint, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                // Pinata V3 API 응답 JSON 파싱 - 새로운 응답 형식에 맞춤
                // 응답 예시: {"data":{"id":"...","name":"file.txt","cid":"bafybeih...","size":4682779,"number_of_files":1,"mime_type":"text/plain","group_id":null}}
                JsonNode root = objectMapper.readTree(response.getBody());

                // 새로운 Pinata V3 API 응답 구조: data.cid에서 CID 추출
                String cid = root.path("data").path("cid").asText();
                if (cid == null || cid.isEmpty()) {
                    log.error("IPFS 응답에 CID가 포함되어 있지 않습니다.");
                    throw new RuntimeException("IPFS 응답에 CID가 포함되어 있지 않습니다.");
                }

                // 추가 정보 로깅 (선택 사항)
                String fileName = root.path("data").path("name").asText();
                long fileSize = root.path("data").path("size").asLong();
                log.info("IPFS에 데이터 저장 성공. 파일명: {}, 크기: {} bytes, CID: {}", fileName, fileSize, cid);

                return new IpfsResponse(cid);
            } catch (JsonProcessingException e) {
                log.error("IPFS 응답 JSON 파싱 실패", e);
                throw new RuntimeException("IPFS 응답 JSON 파싱 실패", e);
            }
        } else {
            log.error("IPFS에 데이터 업로드 실패. 상태 코드: {}", response.getStatusCode());
            throw new RuntimeException("IPFS에 데이터 업로드 실패. 상태 코드: " + response.getStatusCode());
        }
    }

    @Override
    public String get(String ipfsPath) {
        log.info("IPFS에서 공개 데이터를 가져오는 중입니다. CID: {}", ipfsPath);

        try {
            // 1. Pinata 공개 게이트웨이 URL 구성
            String gatewayUrl = "https://" + gatewayDomain + "/ipfs/" + ipfsPath;

            // 2. 단순 GET 요청 생성 및 전송 (공개 파일이므로 인증 불필요)
            ResponseEntity<String> response = restTemplate.getForEntity(gatewayUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("IPFS 데이터 조회 성공. 응답 크기: {} bytes", response.getBody().length());
                return response.getBody();
            } else {
                log.error("IPFS 데이터 조회 실패. 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("IPFS 데이터 조회 실패. 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("IPFS 데이터 조회 중 오류 발생", e);
            throw new RuntimeException("IPFS 데이터 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
