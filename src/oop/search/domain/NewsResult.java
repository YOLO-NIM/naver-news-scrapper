package oop.search.domain;

/// 뉴스 결과 기본 형식
public record NewsResult(
        String title,
        String description,
        String link,
        String originalLink,
        String pubDate) {

}
