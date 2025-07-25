## 🛡️ 2025 [OAuth 2.0] Kakao Social Login Study
> Implementing Kakao Social Login using Spring Boot and OAuth 2.0
### 진행 날짜
~ 2025.07.25
### 진행 내용
- OAuth 1.0의 복잡한 Signature 방식 문제점
  - 요청마다 HMAC-SHA1 기반 Signature 생성 필요
  - URL 인코딩, 파라미터 정렬 등의 까다로운 절차
  - 클라이언트에서 직접 처리 어려움 → 브라우저 기반 서비스에 비효율
- OAuth 2.0의 개선점
  - Signature 제거, Access Token 기반 간소화
  - 대신 HTTPS를 필수화하여 보안 유지
  - Access Token 만료 시 Refresh Token으로 갱신 가능 (Token Lifecycle 관리 명확화)


####  OAuth 2.0 Authorization Code Grant Flow (Kakao)

<img width="1174" height="813" alt="OAuth2.0Flow" src="https://github.com/user-attachments/assets/e271d939-add3-4921-b6b8-1991573ebb31" />

### 실습 환경
- Java: 17
- Spring 3.4.7
- Spring Data JPA - MySQL
- Lombok
- Gradle
