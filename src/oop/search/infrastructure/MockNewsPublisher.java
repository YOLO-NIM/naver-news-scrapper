package oop.search.infrastructure;

import oop.search.application.NewsPublisher;
import oop.search.domain.NewsResult;

import java.util.List;

/// 뉴스를 정상적으로 발행되는지 확인하기 위한 mockup
public class MockNewsPublisher implements NewsPublisher {
    public void publish(String topic, List<NewsResult> newsResults){
        System.out.println("=== 뉴스 발행 테스트 ===");
        System.out.println("검색 키워드: " + topic);
        System.out.println("뉴스 개수: " + newsResults.size());

        for (NewsResult newsResult : newsResults) {
            System.out.println();
            System.out.println("제목: " + newsResult.title());
            System.out.println("설명: " + newsResult.description());
            System.out.println("네이버 링크: " + newsResult.link());
            System.out.println("원문 링크: " + newsResult.originalLink());
            System.out.println("발행일: " + newsResult.pubDate());
        }
    }
}
