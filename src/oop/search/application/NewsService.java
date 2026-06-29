package oop.search.application;

import oop.search.domain.NewsResult;

import java.util.List;

/**
 * 뉴스 검색과 발행 흐름을 제어하는 서비스
 * Provider와 Publisher를 연결
 */
public class NewsService {
    private final NewsProvider newsProvider;
    private final NewsPublisher newsPublisher;

    public NewsService(NewsProvider newsProvider, NewsPublisher newsPublisher){
        this.newsProvider = newsProvider;
        this.newsPublisher = newsPublisher;
    }

    public List<NewsResult> search(String searchQuery, int limit){
        List<NewsResult> newsResults = newsProvider.fetchNews(searchQuery, limit);

        newsPublisher.publish(searchQuery, newsResults);

        return newsResults;
    }
}
