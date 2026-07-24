# MLB Analytics & Community

MLB Stats API와 Baseball Savant 데이터를 기반으로 메이저리그 경기 및 선수 데이터를 수집·분석하는 야구 데이터 분석 플랫폼 프로젝트입니다.

---

## 📌 프로젝트 소개

Spring Boot와 JPA를 기반으로 MLB 팀, 선수, 경기 데이터를 저장하고 경기별 PitchData 및 SprayData를 수집하는 데이터 파이프라인을 구축하고 있습니다.

현재 MLB 공식 API를 연동하여 시즌별 경기 데이터, 선수 정보, 투구 데이터, 타구 데이터를 자동 수집하는 기능을 구현하였으며, 향후 Baseball Savant Statcast CSV를 활용한 고급 야구 분석 기능을 추가할 예정입니다.

---

## 🚀 프로젝트 목표

단순 CRUD 프로젝트를 넘어 MLB 공식 API와 Baseball Savant 데이터를 활용한 실제 스포츠 데이터 분석 플랫폼 구현을 목표로 하고 있습니다.

외부 API 연동, 데이터 파이프라인 구축, 대용량 데이터 처리, 데이터 시각화, 실시간 기능 구현 등의 경험을 쌓기 위해 프로젝트를 진행하고 있습니다.

예외 사항이 있다면 MLB 공식 API에서 제공하지 않는 데이터들은 구현 불가 (오타니 쇼헤이의 2024년도 데이터)

## ⚙️ 기술 스택

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate

### Database
- Postgre SQL
- Redis (예정)

### Frontend
- React
- Next.js

### Tools
- VS Code
- DBeaver
- Git
- GitHub
- Postman
- Docker (예정)

### API / Data
- MLB Stats API
- Baseball Savant Statcast CSV
- JSON Parsing

---

## 📂 현재 구현 기능

### 데이터 수집
- MLB 팀 데이터 수집
- MLB 선수 데이터 수집
- 시즌별 경기 데이터 수집
- PitchData 수집
- SprayData 수집

### PitchData 저장 데이터
- 구종(Pitch Type)
- 구속(Velocity)
- 투구 위치(Plate X / Z)
- 타구 속도(Exit Velocity)
- 발사각(Launch Angle)

### SprayData 저장 데이터
- 타구 좌표(Hit Coordinate X / Y)
- 타구 거리(Hit Distance)
- 타구 방향(Hit Location)
- 이벤트 타입(Single / Double / Triple / Home Run)

### 선수 관련 데이터
- 선수 정보
- 시즌 기록
- 통산 기록
- VS 투수 / VS 타자 기록

### 경기 관련 데이터
- 경기 일정/결과 조회
- 라인스코어
- 박스스코어

### 기타
- 중복 저장 방지 로직 구현
- JSON 데이터 파싱
- 예외 처리 로직 구현
- JPA 연관관계 매핑

---

## 📊 향후 구현 예정 기능

### 경기 기능 (시각화)
- 경기 일정/결과 조회
- 라인스코어
- 박스스코어
- 실시간 경기 데이터

### 선수 기능 (시각화)
- 선수 검색 및 필터
- 시즌 기록
- 통산 기록
- VS 투수 / VS 타자 기록

### 시각화 기능
- Hot / Cold Zone
- Spray Chart
- Pitch Zone
- 구종 분포
- 레이더 차트
- Pitch Movement 분석

### 고급 데이터 분석
- Spin Rate 분석
- Barrel / Hard Hit 분석
- xBA / xSLG 분석
- Baseball Savant CSV 연동

### 커뮤니티 기능
- 실시간 채팅(WebSocket)
- 게시판
- 댓글 / 대댓글
- 좋아요 기능

### 기타
- Redis 캐싱
- Docker 배포
- 서버 배포

---

## 🗂️ 프로젝트 구조

```bash
src
 ┣ config
 ┣ controller
 ┣ domain
 ┣ dto
 ┣ repository
 ┣ service
 ┗ global
