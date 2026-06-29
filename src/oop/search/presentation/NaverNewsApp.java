package oop.search.presentation;

import oop.search.application.NewsService;
import oop.search.infrastructure.MockNewsPublisher;
import oop.search.infrastructure.NaverNewsProvider;

public class NaverNewsApp {
    public static void main(String[] args) {
        NewsService newsService = new NewsService(
                new NaverNewsProvider(),
                new MockNewsPublisher()
        );

        newsService.search("자바", 2);
    }
}
