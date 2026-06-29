package oop.search.infrastructure;

import oop.search.application.NewsPublisher;
import oop.search.domain.NewsResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/// 뉴스 검색 결과를 GitHub Issue로 발행
public class GitHubNewsPublisher extends AbstractHttpClient implements NewsPublisher {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/%s/issues";

    private final String token;

    public GitHubNewsPublisher() {
        super(GITHUB_API_URL.formatted(Env.getRequired("GITHUB_REPOSITORY")));
        this.token = Env.getRequired("GITHUB_TOKEN");
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

            if(response.statusCode() != 201){
                throw new IllegalStateException(
                        "GitHub Issue 생성 실패. statusCode = " + response.statusCode() + ", body = " + response.body()
                );
            }

            System.out.println("GitHub API statusCode = " + response.statusCode());
        }catch (IOException e){
            throw new IllegalStateException("GitHub API 요청 중 입출력 오류가 발생하였습니다.", e);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IllegalStateException("GitHub API 요청이 중단되었습니다.", e);
        }
    }

    /// 뉴스 body 생성
    private String buildIssueBody(String topic, List<NewsResult> newsResults) {
        StringBuilder sb = new StringBuilder();

        sb.append("# 📰 ").append(topic).append(" 뉴스 스크랩").append("\n\n");
        sb.append("- 검색어: `").append(topic).append("`").append("\n");
        sb.append("- 수집 뉴스 개수: ").append(newsResults.size()).append("개").append("\n");
        sb.append("- 생성일: ")
                .append(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate())
                .append("\n\n");
        sb.append("---").append("\n\n");

        for (int i = 0; i < newsResults.size(); i++) {
            NewsResult newsResult = newsResults.get(i);

            sb.append("## ").append(i + 1).append(". ")
                    .append(newsResult.title())
                    .append("\n\n");

            sb.append("> ")
                    .append(newsResult.description())
                    .append("\n\n");

            sb.append("- 🔗 네이버 링크: ")
                    .append(newsResult.link())
                    .append("\n");

            if (!newsResult.link().equals(newsResult.originalLink())) {
                sb.append("- 📰 원문 링크: ")
                        .append(newsResult.originalLink())
                        .append("\n");
            }

            sb.append("- 🗓 발행일: ")
                    .append(formatPubData(newsResult.pubDate()))
                    .append("\n\n");

            if (i < newsResults.size() - 1) {
                sb.append("---").append("\n\n");
            }
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

    /// 날짜 포맷팅
    private String formatPubData(String pubData){
        try{
            ZonedDateTime dateTime = ZonedDateTime.parse(
                    pubData,
                    DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ENGLISH)
            );
            return dateTime.format(
                    DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN)
            );
        }catch (DateTimeParseException e){
            return pubData;
        }
    }
}
