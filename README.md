## 프로젝트 구조

📦 naver-news-scraper  
┣ 📂 src  
┃ ┗ 📂 oop  
┃ ┗ 📂 search  
┃ ┣ 📂 application # 기능 흐름 및 인터페이스  
┃ ┣ 📂 domain # 핵심 데이터(뉴스 결과, 정렬 옵션 등)  
┃ ┣ 📂 infrastructure # 외부 연동(네이버, GitHub API)  
┃ ┗ 📂 presentation # 실행 진입점  
┣ 📄 .env.sample  
┣ 📄 .gitignore  
┗ 📄 README.md

---
## 패키지 역할

| 패키지              | 역할                                            |
|------------------|-----------------------------------------------|
| `domain`         | 뉴스 결과, 정렬 옵션처럼 프로젝트의 핵심 데이터를 정의               |
| `application`    | 뉴스 검색과 발행 흐름을 제어하는 서비스와 인터페이스를 정의             |
| `infrastructure` | 네이버 뉴스 API, GitHub Issues API 같은 외부 연동 기능을 구현 |
| `presentation`   | 프로젝트 실행 진입점을 관리                               |
---

## 추가된 기능
### 1. 환경변수 검증
- infrastructure/Env.java를 추가하여 환경변수 검증 기능을 구현
- 필수 환경변수가 없거나 비어 있을 경우, API 요청 전에 예외를 발생시키도록 변경
- 숫자 타입 환경변수(`NEWS_DISPLAY`)와 enum 타입 환경변수(`NEWS_CATEGORY`)도 별도로 검증

### 2. API 응답 상태 코드 검증
- 네이버 뉴스 API 응답 코드가 200이 아닐 경우 예외 발생
- GitHub Issues API 응답 코드가 201이 아닐 경우 예외 발생
- 실패 상황에서도 프로그램이 정상 종료되지 않도록 예외를 다시 던지는 방식으로 수정

### G3. itHub Issue 본문 포맷 개선
- 뉴스 목록을 GitHub Markdown 형식으로 더 읽기 좋게 출력
- 검색어, 수집 개수, 생성일을 상단에 요약
- 뉴스 설명은 인용문 형태로 표시
- 네이버 링크와 원문 링크가 같을 경우 중복 출력을 생략
- 발행일을 `yyyy년 MM월 DD일 HH:mm` 형태로 변환
---

## 코드 작성 시 의문 사항 정리
### 1. NewsResult
Q :  네이버 뉴스 API 기준으로 내용을 고도화 하고 싶으면 객체에 어떤 데이터를 추가하는게 좋을까?

A: 네이버 뉴스 API 기준으로 개별 뉴스 결과는 아래 필드를 가진다.

| API 필드         | 의미              | 현재 반영 여부 |
| -------------- | --------------- | -------- |
| `title`        | 뉴스 제목           | 반영       |
| `description`  | 뉴스 요약 설명        | 반영       |
| `link`         | 네이버 뉴스 또는 뉴스 링크 | 반영 |
| `originallink` | 언론사 원문 링크       | 반영       |
| `pubDate`      | 발행일             | 반영    |

우선은 네이버 API가 제공하는 구조를 따라가는 정도로만 업그레이드하는 것을 추천한다.

---
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

A: 개념적으로는 비슷하게 이해해도 된다.

SIM("sim", "정확도순")은 SIM이라는 enum 상수를 만들면서 생성자에 "sim"과 "정확도순"을 전달하는 코드이다.

개념적으로는 아래와 비슷하다.

```java
public static final NewsCategory SIM =
new NewsCategory("sim", "정확도순");
```

다만 enum의 생성자는 외부에서 new로 호출할 수 없기 때문에, 직접 아래처럼 작성하는 것은 불가능하다.
```java
NewsCategory SIM = new NewsCategory("sim", "정확도순");
```

---
### 3. NewsProvider와 NewsPublisher
Q : NewsProvider와 NewsPublisher의 둘 다  검색 키워드를 받아서 작동하는데, 둘의 차이가 뭐야?

A: 핵심 차이는 다음과 같다.  
#### NewsProvider
* 뉴스를 가져오는 역할
* input: 검색어
* output: 뉴스 목록
* 관심사는 **수집**

#### NewsPublisher
* 가져온 뉴스를 외부로 발행하는 역할
* input: 검색어 + 뉴스 목록
* output: 발행 결과
* 관심사는 **발행**
* `topic`은 다시 검색하기 위한 값이 아니라, GitHub Issue 제목이나 본문 주제로 사용하기 위한 값

즉, 전체 흐름은 아래와 같다.

NewsProvider  → 뉴스 수집  
NewsService   → 수집과 발행 흐름 제어  
NewsPublisher → 뉴스 발행

---
### 4. AbstractHttpClient
Q1 : HTTP 요청을 보낼 때마다 AbstractHttpClient를 활용한다고 하는데, 그럼 카카오나 구글 같은 다른 API를 사용할 때도 이 클래스를 사용하는거야?

A: 다른 외부 API를 Java HttpClient로 직접 호출한다면 이 클래스를 사용할 수 있다.
다만 아래와 같은 상황이라면 굳이 상속할 필요가 없다.
```
1. HTTP 요청을 직접 보내지 않는 경우
2. 공식 SDK를 사용하는 경우
3. 파일 읽기, DB 접근, 콘솔 출력처럼 HTTP와 관련 없는 기능인 경우
4. 공통으로 재사용할 코드가 거의 없는 경우
```
즉, “외부 API를 사용한다 = 무조건 AbstractHttpClient를 상속한다”는 뜻은 아니다.

Q2 : 코드 내에 있는 `endpoint`가 뭐야?

A : `endpoint`는 **API 요청을 보낼 기본 목적지 주소**이다.

---
### 5. NaverNewsProvider
Q: cleanText의 진행 순서가 newsResult의 입력 변수 순서랑 다른데, 이건 원본 데이터의 형태 때문인거야?

A: 맞다. 원본 JSON 데이터의 필드 순서와 NewsResult의 필드 순서가 달라서 생긴 차이다.

네이버 API 원본 응답은 대략 아래 순서로 들어온다.
```json
{
  "title": "...",
  "originallink": "...",
  "link": "...",
  "description": "...",
  "pubDate": "..."
}
```
하지만 현재 `NewsResult`는 아래 순서로 정의되어 있다.
```java
public record NewsResult(
        String title,
        String description,
        String link,
        String originalLink,
        String pubDate
){}
```
변수를 추출하는 순서는 크게 중요하지 않지만, NewsResult 생성자에 값을 넣을 때는 반드시 record에 정의된 순서를 지켜야 한다.

---
### 6. Env
Q : 제너릭 문법 `<T extends Enum<T>> T`에 대해서 설명해줘

A :
아래처럼 이해하면 된다.
```
T라는 타입을 사용할 건데,
그 T는 반드시 enum 타입이어야 하고,
이 메서드는 그 enum 타입의 값을 반환한다.
```
뒤에 있는 T는 반환 타입입니다.

---
### 7. Exception 처리
Q : 아래 코드는 스레드를 강제 종료시키는 코드야?
```java
Thread.currentThread().interrupt();
```
A. 아니다.

이 코드는 강제 종료가 아니라, 현재 스레드의 중단 상태를 다시 표시하는 코드이다.

`InterruptedException`이 발생하면 현재 스레드의 interrupt 상태가 초기화될 수 있기 때문에, catch 블록 안에서 다시 중단 상태를 표시해주는 것이다.

실제로 프로그램을 실패로 종료시키는 역할은 아래 코드가 담당한다.

```java
throw new IllegalStateException("요청이 중단되었습니다.", e);
```

즉, 역할을 나누면 아래와 같다.

`Thread.currentThread().interrupt()`
→ 현재 스레드가 중단 요청을 받았다는 상태를 다시 표시

`throw new IllegalStateException(...)`
→ 예외를 다시 던져 프로그램 실패 처리