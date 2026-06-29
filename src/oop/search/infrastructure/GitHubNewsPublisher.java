package oop.search.infrastructure;

import oop.search.application.NewsPublisher;
import oop.search.domain.NewsResult;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/// 뉴스 검색 결과를 GitHub Issue로 발행
public class GitHubNewsPublisher extends AbstractHttpClient implements NewsPublisher {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/%s/issues";

    private final String token;

    public GitHubNewsPublisher() {
        super(GITHUB_API_URL.formatted(System.getenv("GITHUB_REPOSITORY")));
        this.token = System.getenv("GITHUB_TOKEN");
    }

    public void publish(String topic, List<NewsResult> newsResults){
        String title = "%s 뉴스 스크랩 (%s)".formatted(
                topic,
                ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate()
        );

        String body = buildIssueBody(topic, newsResults);

        String payload = """
                {
                    "title": "%s",
                    "body": "%s"
                }
                """.formatted(
                escapeJson(title),
                escapeJson(body)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("github statusCode = " + response.statusCode());
            System.out.println("github body = " + response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /// 뉴스 body 생성
    private String buildIssueBody(String topic, List<NewsResult> newsResults){
        StringBuilder sb = new StringBuilder();

        sb.append("# ").append(topic).append(" 뉴스 스크랩").append("\n\n");
        sb.append("수집 뉴스 개수: ").append(newsResults.size()).append("개").append("\n\n");

        for (int i = 0; i < newsResults.size(); i++) {
            NewsResult newsResult = newsResults.get(i);

            sb.append("## ").append(i + 1).append(". ").append(newsResult.title()).append("\n\n");
            sb.append("- 설명: ").append(newsResult.description()).append("\n");
            sb.append("- 네이버 링크: ").append(newsResult.link()).append("\n");

            if(!newsResult.link().equals(newsResult.originalLink())){
                sb.append("- 원문 링크: ").append(newsResult.originalLink()).append("\n");
            }

            sb.append("- 발행일: ").append(newsResult.pubDate()).append("\n\n");
        }

        return sb.toString();
    }

    private String escapeJson(String text){
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
