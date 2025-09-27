# UltimateCashShop (Minecraft 1.21.1 / Java 21)

**Arclight/Paper/Spigot 호환** 캐시 상점 플러그인입니다.  
/캐시 (잔액/지급/차감/보내기/랭킹), /캐시상점 (열기/등록/취소), Citizens NPC 우클릭 연동(옵션)을 제공합니다.

## 빌드 (Maven)
```bash
mvn -q -e -DskipTests package
```
생성물: `target/UltimateCashShop-1.21.1-1.0.0.jar`

- JDK 21 필요
- `io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT` 제공범위 의존성 사용
- 서버: Paper/Spigot/Arclight 1.21.1 (Java 21) 권장

## 주요 데이터 파일
- `plugins/UltimateCashShop/balances.yml` : 유저 캐시
- `plugins/UltimateCashShop/shop.yml` : 상점 슬롯/아이템/가격/수량
- `plugins/UltimateCashShop/links.yml` : Citizens NPC 연결 목록

## 명령어
- `/캐시` : 내 잔액 확인
- `/캐시 <플레이어>` : 해당 유저 잔액 확인
- `/캐시 랭킹` : 상위 랭킹 출력
- `/캐시 보내기 <플레이어> <수량>` : 플레이어에게 캐시 송금
- `/캐시 지급 <플레이어> <수량>` : (관리자) 지급
- `/캐시 차감 <플레이어> <수량>` : (관리자) 차감
- `/캐시상점` : GUI 열기
- `/캐시상점 등록 <슬롯> <가격> <수량>` : 손에 든 아이템 등록
- `/캐시상점 취소 <슬롯>` : 등록 취소
- `/캐시상점 링크 <NPC_ID>` : Citizens NPC와 연동
- `/캐시상점 링크해제 <NPC_ID>` : 연동 해제

## 주의
- `config.yml`의 `gui.size`는 9의 배수여야 합니다. 기본 54.
- Citizens는 **옵션**입니다. 설치되어 있지 않으면 관련 기능이 비활성화됩니다.
