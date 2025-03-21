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
    private final String apiKey;
    private final String apiKeySecret;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(IpfsClientImpl.class);

    public IpfsClientImpl(@Value("${IPFS_API_URL}") String ipfsApiUrl,
                          @Value("${INFURA_API_KEY}") String apiKey,
                          @Value("${INFURA_API_KEY_SECRET}") String apiKeySecret) {
        this.restTemplate = new RestTemplate();
        this.ipfsApiUrl = ipfsApiUrl;
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.apiKeySecret = apiKeySecret;
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

        // 2. multipart/form-data용 헤더 설정 및 Basic Auth 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(apiKey, apiKeySecret);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 3. IPFS API 엔드포인트로 POST 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(ipfsApiUrl, requestEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                // IPFS API 응답 JSON 파싱
                // 응답 예시: {"Name":"data.json","Hash":"QmDummyCidExample","Size":"123"}
                JsonNode root = objectMapper.readTree(response.getBody());
                String cid = root.path("Hash").asText();
                if (cid == null || cid.isEmpty()) {
                    log.error("IPFS 응답에 CID가 포함되어 있지 않습니다.");
                    throw new RuntimeException("IPFS 응답에 CID가 포함되어 있지 않습니다.");
                }
                log.info("IPFS에 데이터 저장 성공. CID: {}", cid);
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
        // URL 빌더를 통해 쿼리 파라미터 설정
        log.info("IPFS에서 데이터를 가져오는 중입니다. 경로: {}", ipfsPath);
        String url = UriComponentsBuilder
                .fromHttpUrl(ipfsApiUrl)
                .queryParam("arg", ipfsPath)
                .toUriString();

        // Basic 인증 헤더 생성
        String auth = apiKey + ":" + apiKeySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);

        // 헤더를 포함하는 HTTP 엔티티 생성 (본문은 필요없으므로 null 사용)
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Infura의 GET API는 POST 방식으로 호출합니다.
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("IPFS 데이터 조회 성공. 응답 크기: {} bytes",
                    response.getBody() != null ? response.getBody().length() : 0);
            return response.getBody();
        } else {
            log.error("IPFS 컨텐츠 조회 실패. 상태 코드: {}", response.getStatusCode());
            throw new RuntimeException("IPFS 컨텐츠 조회 실패: " + response.getStatusCode());
        }
    }
}
