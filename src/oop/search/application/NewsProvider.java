package oop.search.application;

import oop.search.domain.NewsResult;

import java.util.List;

/// 뉴스를 가져오는 기능의 규격 생성
/// 뉴스를 가져오는 역할
public interface NewsProvider {
    /// searchQuery : 검색어 / limit : 검색 개수
    /// 검색어와 검색 개수를 전달하면, 뉴스 결과 목록을 반환한다.
    List<NewsResult> fetchNews(String searchQuery, int limit);
}
