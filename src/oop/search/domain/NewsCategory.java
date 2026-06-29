package oop.search.domain;

/// 뉴스 검색 정렬 옵션
public enum NewsCategory {
    SIM("sim", "정확도순"),
    DATE("date", "최신순");

    private final String queryValue;
    private final String description;

    /// enum 생성자는 외부에서 호출할 수 없기 때문에, 명시적으로 private를 붙여도 된다. => 스타일 차이!
    private NewsCategory(String queryValue, String description){
        this.queryValue = queryValue;
        this.description = description;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public String getDescription(){
        return description;
    }
}
