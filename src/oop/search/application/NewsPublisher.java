package oop.search.application;

import oop.search.domain.NewsResult;

import java.util.List;

/// 가져온 뉴스를 내보내는 역할
public interface NewsPublisher {
    /// topic : 뉴스 검색 키워드 / newsResults : 뉴스 검색 결과 목록
    /// 검색 키워드와 뉴스 결과 목록을 받아서, 외부에 발행한다.
    void publish(String topic, List<NewsResult> newsResults);
}
