package oop.search.infrastructure;

import java.net.http.HttpClient;

/// HTTP 요청을 보내는 클래스들이 공통으로 사용할 부모 클래스
public abstract class AbstractHttpClient {
    protected final HttpClient httpClient = HttpClient.newHttpClient();
    protected final String endpoint; /// 요청을 보낼 API 주소

    protected AbstractHttpClient(String endpoint){
        this.endpoint = endpoint;
    }
}
