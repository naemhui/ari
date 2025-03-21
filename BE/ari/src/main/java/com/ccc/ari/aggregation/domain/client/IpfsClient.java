package com.ccc.ari.aggregation.domain.client;

/**
 * IPFS 연동 인터페이스
 * 데이터를 IPFS에 저장하고 CID를 반환하는 역할을 추상화합니다.
 */
public interface IpfsClient {

    /**
     * 주어진 데이터를 IPFS에 저장하고, 해당 데이터의 CID를 반환한다.
     *
     * @param data 직렬화된 문자열 데이터
     * @return IpfsResponse 객체 (CID값을 포함)
     */
    IpfsResponse save(String data);

    /**
     * 주어진 IPFS 경로의 컨텐츠를 조회한다.
     *
     * @param ipfsPath IPFS 객체 해시(CID)
     * @return 조회된 컨텐츠 (text/plain)
     */
    String get(String ipfsPath);
}
