package oop.search.infrastructure;

import oop.search.application.NewsProvider;
import oop.search.domain.NewsCategory;
import oop.search.domain.NewsResult;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/// 네이버 뉴스 API를 호출해 뉴스 검색 결과를 가져오는 Provider
public class NaverNewsProvider extends AbstractHttpClient implements NewsProvider {
    private static final String NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    private final String clientId;
    private final String clientSecret;
    private final NewsCategory category;

    public NaverNewsProvider(){
        super(NEWS_API_URL);
        this.clientId = System.getenv("NAVER_CLIENT_ID");
        this.clientSecret = System.getenv("NAVER_CLIENT_SECRET");
        this.category = NewsCategory.DATE;
    }

    @Override
    public List<NewsResult> fetchNews(String searchQuery, int limit) {
        /// url 생성
        String url = endpoint
                + "?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)
                + "&display=" + limit
                + "&start=1"
                + "&sort=" + category.getQueryValue();

        /// http 요청 전송
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        List<NewsResult> results = new ArrayList<>();

        /// http 응답 수신
        try{
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            /// 정상 수신 확인
            System.out.println("statusCode = " + response.statusCode());

            String body = response.body();

            String itemsText = body
                    .split("\"items\":\\[", 2)[1]
                    .split("\\]\\s*\\}", 2)[0];

            String[] itemArray = itemsText.split("\\},\\s*\\{");

            for(String item : itemArray){
                String title = cleanText(cutText(item, "\"title\":\"", "\","));
                String description = cleanText(cutText(item, "\"description\":\"", "\","));
                String link = cleanText(cutText(item, "\"link\":\"", "\","));
                String originalLink = cleanText(cutText(item, "\"originallink\":\"", "\","));
                String pubDate = cleanText(cutText(item, "\"pubDate\":\"", "\""));

                NewsResult newsResult = new NewsResult(
                        title,
                        description,
                        link,
                        originalLink,
                        pubDate
                );

                results.add(newsResult);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return results;
    }

    private String cutText(String original, String prefix, String suffix){
        int startIndex = original.indexOf(prefix);

        if(startIndex == -1){
            return "";
        }

        startIndex += prefix.length();

        int endIndex = original.indexOf(suffix, startIndex);

        if(endIndex == -1){
            return "";
        }

        return original.substring(startIndex, endIndex);
    }

    private String cleanText(String text){
        return text
                .replace("\\/", "/")
                .replace("<b>", "")
                .replace("</b", "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .trim();
    }
}

