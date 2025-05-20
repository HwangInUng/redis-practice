# Redis 실습 - 1

## Sorted set 활용 리더보드 구현

### 개요
> 리더보드는 순위를 매기는 데이터 구조로 많은 서비스에서 사용된다. Sorted Set의 특성을 활용하여 리더보드를 구현한다.

### 목표
- 상대적 리더보드를 구현
- 플레이어별 점수 추가, 수정, 조회, 삭제 기능 구현
- 그룹 내 순위 조회 기능 구현

### 특징
- 사용자의 스코어를 기반으로 데이터를 정렬하는 기능
- 사용자 증가에 따라 가공해야 할 데이터 증가 폭이 큼
- 실시간으로 반영돼야 하는 데이터의 특성을 가짐

### 요구사항
- 사용자 점수 추가
- 사용자 점수 수정
- 사용자 삭제
- 랭킹 합산

### API
#### 사용자 점수 추가
**Request**
```bash
# URL
POST /player

# body
{
  "id": 1
  "name": "player1",
  "score": 100
},
{
  "id": 2
  "name": "player2",
  "score": 90
},
{
  "id": 3
  "name": "player3",
  "score": 210
},
{
  "id": 4
  "name": "player4",
  "score": 120
},
{
  "id": 5
  "name": "player5",
  "score": 130
}
```
**Response**
성공
```json
{
  "status": 200,
  "message": "사용자 점수 등록 성공"
}
```
실패 - 사용자 점수가 없는 경우
```json
{
  "status": 400,
  "message": "일부 사용자의 점수가 누락되었습니다."
}
```
#### 사용자 점수 수정
**Request**
```bash
# URL
PUT /player

# body
{
  "id": 1,
  "name": "player1",
  "score": 200
}
```

**Response**
성공
```json
{
  "status": 200,
  "message": "사용자 점수 수정 성공"
}
```
실패 - 수정 대상이 없는 경우
```json
{
  "status": 400,
  "message": "수정 대상이 없습니다."
}
```
실패 - 수정할 점수가 없는 경우
```json
{
  "status": 400,
  "message": "수정할 점수가 입력되지 않았습니다."
}
```

#### 사용자 삭제
**Request**
```bash
DELETE /player/{id}
```
**Response**
성공
```json
{
  "status": 200,
  "message": "사용자 삭제 성공"
}
```
실패 - 삭제할 사용자가 없는 경우
```json
{
  "status": 400,
  "message": "삭제할 사용자가 없습니다."
}
```

#### 랭킹 합산
**Request**
```bash
# URL
GET /leaderboard
```

**Response**
성공
```json
{
  "status": 200,
  "leaderboard": [
    {
      "rank": 1,
      "name": "player3",
      "score": 210
    },
    ...
  ]
}
```