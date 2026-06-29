## 프로젝트 구조

📦 my-project  
┣ 📂 src  
┃ ┣ 📂 application # 기능 흐름 및 인터페이스  
┃ ┣ 📂 domain  # 핵심 데이터(뉴스 결과, 정렬 옵션 등)  
┃ ┣ 📂 infrastructure # 외부 연동(네이버, Github API)  
┃ ┣ 📂 presentation # 콘솔 앱, 실행 진입점

## 패키지 역할

| 패키지              | 역할                                            |
|------------------|-----------------------------------------------|
| `domain`         | 뉴스 결과, 정렬 옵션처럼 프로젝트의 핵심 데이터를 정의               |
| `application`    | 뉴스 검색과 발행 흐름을 제어하는 서비스와 인터페이스를 정의             |
| `infrastructure` | 네이버 뉴스 API, GitHub Issues API 같은 외부 연동 기능을 구현 |
| `presentation`   | 프로젝트 실행 진입점을 관리                               |

## 코드 작성 시 의문 사항 정리
### 1. NewsResult
Q :  네이버 뉴스 API 기준으로 내용을 고도화 하고 싶으면 객체에 어떤 데이터를 추가하는게 좋을까?

A: 네이버 뉴스 API 기준, 기본 응답은 아래에 가깝다.

| API 필드         | 의미              | 현재 반영 여부  |
| -------------- | --------------- | --------- |
| `title`        | 뉴스 제목           | 반영        |
| `description`  | 뉴스 요약 설명        | 반영        |
| `link`         | 네이버 뉴스 또는 뉴스 링크 | `url`로 반영 |
| `originallink` | 언론사 원문 링크       | 미반영       |
| `pubDate`      | 발행일             | 반영 예정     |

우선은 네이버 API가 제공하는 구조를 따라가는 정도로만 업그레이드하는 것을 추천한다.
### 2. NewsCategory
Q : 
```java
SIM("sim", "정확도순"), 
DATE("date", "최신순"); 
```
 이 부분은 무슨 역할을 하는 코드야?
 ```java
 NewsCategory SIM = new NewsCategory("sim", "정확도순"); 
 ```
 
이런 느낌으로 보면 될까?

A: 개념적으로는 아주 좋은 이해다. 해당 코드는 Java가 내부적으로 enum 상수 객체를 생성하는 문법이라고 보면 된다.
```java
public static final NewsCategory SIM =
        new NewsCategory("sim", "정확도순");
```
다만 enum의 생성자는 외부에서 new로 호출할 수 없어 예시로 적어준 코드는 작동은 불가능하다.  



### 3. NewsPublisher
Q : NewsProvider와 NewsPublisher의 둘 다 뭔가 검색 키워드를 받아서 작동하는데, 둘의 차이가 뭐야?

A: 핵심 차이는 다음과 같다.
* NewsProvider
  * 뉴스를 가져오는 역할
  * `input`: 검색어 / `output`: 뉴스 목록
  * 관심사는 '수집'
  
* NewsPublisher 
  * 가져온 뉴스를 내보내는 역할
  * `input`: 검색어 + 뉴스 목록 / `output`: 발행 결과
  * 관심사는 '발행'
  * NewsPublisher의 `topic`은 다시 검색하기 위한 값이 아닌 발행할 때 제목이나 주제로 쓰기 위한 값

### 4. AbstractHttpClient
Q1 : HTTP 요청을 보낼 때마다 AbstractHttpClient를 활용한다고 하는데, 그럼 카카오나 구글 같은 다른 API를 사용할 때도 이 클래스를 사용하는거야?

A: 다른 외부 API를 Java HttpClient로 직접 호출한다면 이 클래스를 사용할 수 있다.
다만, "외부 API를 쓴다 = 무조건 이 클래스를 상속한다"가 아니다. 
```
1. HTTP 요청을 직접 보내지 않는다
2. 공식 SDK를 사용한다
3. 파일 읽기, DB 접근, 콘솔 출력처럼 HTTP와 관련 없는 기능이다
4. 공통으로 재사용할 코드가 거의 없다
```
위와 같은 상황이면 굳이 상속할 필요가 없다.

Q2 : 코드 내에 있는 `endpoint`가 뭐야?

A : `endpoint`는 **API 요청을 보낼 기본 목적지 주소**이다.

### 5. NaverNewsApp
Q: cleanText의 진행 순서가 newsResult의 입력 변수 순서랑 다른데, 이건 원본 데이터의 형태 때문인거야?

A: 정확하다. 그 차이는 원본 JSON 데이터의 필드 순서와 NewsResult의 필드 순서가 달라서 생긴 것이다.
* 원본 body
```json
{
  "title": "...",
  "originallink": "...",
  "link": "...",
  "description": "...",
  "pubDate": "..."
}
```
* NewsResult
```java
public record NewsResult(
        String title,
        String description,
        String link,
        String originalLink,
        String pubDate
){}
```
변수를 뽑는 순서는 크게 중요하지 않으므로, 불편하다면 변수 추출 순서를 변경하도 괜찮다.