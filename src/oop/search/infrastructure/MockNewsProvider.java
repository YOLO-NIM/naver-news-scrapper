package oop.search.infrastructure;

import oop.search.application.NewsProvider;
import oop.search.domain.NewsResult;

import java.util.List;

/// 뉴스를 가져오는 역할이 정상적으로 연결되는지 확인하기 위한 mockup
public class MockNewsProvider implements NewsProvider {
    @Override
    public List<NewsResult> fetchNews(String searchQuery, int limit) {
        return List.of(
                new NewsResult(
                        searchQuery + "관련 뉴스 1",
                        "테스트용 뉴스 설명입니다.",
                        "https://news.naver.com/example-1",
                        "https://original-news.example.com/article-1",
                        "Mon, 29 Jun 2026 10:00:00 +0900"
                ),
                new NewsResult(
                        searchQuery + "관련 뉴스 2",
                        "두 번째 테스트용 뉴스 설명입니다.",
                        "https://news.naver.com/example-2",
                        "https://original-news.example.com/article-2",
                        "Mon, 29 Jun 2026 10:00:00 +0900"
                )
        );
    }
}
