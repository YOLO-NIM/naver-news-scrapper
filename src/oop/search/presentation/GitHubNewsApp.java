package oop.search.presentation;

import oop.search.application.NewsService;
import oop.search.infrastructure.Env;
import oop.search.infrastructure.GitHubNewsPublisher;
import oop.search.infrastructure.NaverNewsProvider;

public class GitHubNewsApp {
    public static void main(String[] args) {
        NewsService newsService = new NewsService(
                new NaverNewsProvider(),
                new GitHubNewsPublisher()
        );

        String searchQuery = Env.getRequired("NEWS_QUERY");
        int limit = Env.getRequiredInt("NEWS_DISPLAY");

        newsService.search(searchQuery, limit);
    }
}
