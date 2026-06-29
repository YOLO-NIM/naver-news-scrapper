package oop.search.presentation;

import oop.search.application.NewsService;
import oop.search.infrastructure.MockNewsProvider;
import oop.search.infrastructure.MockNewsPublisher;

public class MockNewsApp {
    public static void main(String[] args) {
        /// 전체 흐름 제어
        NewsService newsService = new NewsService(
                new MockNewsProvider(), /// 뉴스 가져오기 역할
                new MockNewsPublisher() /// 뉴스 발행 역할
        );

        newsService.search("자바", 2);
    }
}
