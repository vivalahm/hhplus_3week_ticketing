### 인덱스 성능 개선 보고서

### 1. 서론

데이터베이스 쿼리 성능 최적화를 위해 두 가지 시나리오에서 인덱스를 추가하여 성능 변화를 분석했습니다. 첫 번째 시나리오는 `Seat` 테이블의 `concert_option_id`와 `status` 컬럼에 인덱스를 추가하는 것이었고, 두 번째 시나리오는 `Concert_Option` 테이블의 `concert_id`, `concert_date`, `is_available` 컬럼에 인덱스를 추가하는 것이었습니다. 각 시나리오에 대해 쿼리 실행 계획과 성능 변화를 비교 분석했습니다.

### 2. 분석 데이터

**쿼리 1:**

```sql
SELECT s.* FROM Seat s WHERE s.concert_option_id = 1 AND s.status='AVAILABLE';
```

**쿼리 2:**

```sql
SELECT * FROM Concert_Option WHERE concert_id = 1 AND concert_date > '2024-08-08 22:24:11.000000' AND is_available = TRUE;
```

### 3. 성능 분석

### 3.1 `Seat` 테이블 인덱스 전후 성능 비교

**인덱스 전**

![image](https://github.com/user-attachments/assets/1fd94476-543e-40e4-aec3-7386419a9b67)

- 쿼리 실행 계획

  :

    - `Parallel Seq Scan` on `seat` 테이블
    - **필터 조건**: `concert_option_id = 1`, `status = 'AVAILABLE'`
    - **실행 시간**: 324.931 ms
    - **처리된 행 수**: 1422767 행 중 43899 행

**인덱스 후**

![image](https://github.com/user-attachments/assets/9b2eaeee-0f56-4957-a329-4b1acb3099be)

- 쿼리 실행 계획

  :

    - `Bitmap Heap Scan` on `seat` 테이블
    - **필터 조건**: `concert_option_id = 1`, `status = 'AVAILABLE'`
    - **실행 시간**: 38.922 ms
    - **처리된 행 수**: 133445 행 중 133445 행

**결론**

- 인덱스를 추가한 후 쿼리 실행 시간이 약 324.931 ms에서 38.922 ms로 약 8배 이상 감소했습니다. 이는 인덱스를 사용하여 필요한 데이터에 빠르게 접근할 수 있었기 때문입니다.

### 3.2 `Concert_Option` 테이블 인덱스 전후 성능 비교

**인덱스 전**

![image](https://github.com/user-attachments/assets/0c319126-c3f6-4cee-913d-4b552c18cf29)

- 쿼리 실행 계획

  :

    - `Seq Scan` on `concert_option` 테이블
    - **필터 조건**: `concert_id = 1`, `concert_date > '2024-08-08 22:24:11'`, `is_available = TRUE`
    - **실행 시간**: 10.645 ms
    - **처리된 행 수**: 400 행 중 39600 행

**인덱스 후**

![image](https://github.com/user-attachments/assets/aacf6030-c6be-49e7-80f7-f17c9fe0a99a)

- 쿼리 실행 계획

  :

    - `Bitmap Heap Scan` on `concert_option` 테이블
    - **필터 조건**: `concert_id = 1`, `concert_date > '2024-08-08 22:24:11'`, `is_available = TRUE`
    - **실행 시간**: 0.965 ms
    - **처리된 행 수**: 400 행 중 400 행

**결론**

- 인덱스를 추가한 후 쿼리 실행 시간이 약 10.645 ms에서 0.965 ms로 약 10배 이상 감소했습니다. 이는 인덱스를 사용하여 필요한 데이터에 빠르게 접근할 수 있었기 때문입니다.

### 4. 결론 및 제언

두 시나리오 모두에서 인덱스를 추가함으로써 쿼리 성능이 크게 향상되었습니다. 특히, `Seat` 테이블과 `Concert_Option` 테이블에 인덱스를 추가한 후 쿼리 실행 시간이 각각 약 8배, 10배 이상 감소하였습니다. 이는 인덱스를 사용하여 필요한 데이터에 빠르게 접근할 수 있었기 때문입니다.

### 5. 인덱스 생성 구문

```sql
-- Seat 테이블 인덱스 생성
DROP INDEX IF EXISTS idx_seat_concert_option_id_status;
CREATE INDEX idx_seat_concert_option_id_status ON seat (concert_option_id, status);
VACUUM ANALYZE seat;

-- Concert_Option 테이블 인덱스 생성
DROP INDEX IF EXISTS idx_concert_option_concert_id_date_available;
CREATE INDEX idx_concert_option_concert_id_date_available ON concert_option (concert_id, concert_date, is_available);
VACUUM ANALYZE concert_option;
```

## 6. 조인 쿼리

#### 모든 콘서트에 조회 시 콘서트 옵션을 조인해서 조회

### 인덱싱 전

1. **쿼리**:

   ```sql
   SELECT c.title, co.concert_date, co.is_available
   FROM Concert c
   JOIN Concert_Option co ON c.id = co.concert_id;
   ```

   ![image](https://github.com/user-attachments/assets/36736977-abfc-4572-9297-384172512ab6)

2. **쿼리 실행 계획**:

    - **Hash Join**: `concert`와 `concert_option` 테이블 간 해시 조인.
    - **Hash Cond**: `co.concert_id = c.id`
    - **Seq Scan on concert_option**: `concert_option` 테이블에 대한 시퀀스 스캔.
    - **Seq Scan on concert**: `concert` 테이블에 대한 시퀀스 스캔.
    - **실행 시간**: 46.836 ms
    - **처리된 행 수**: 40000 행

### 인덱싱 후

![image](https://github.com/user-attachments/assets/afc56cc1-4e25-457e-8e17-489275ea0526)

1. **쿼리**: 동일.

2. 쿼리 실행 계획

   :

    - **Hash Join**: `concert`와 `concert_option` 테이블 간 해시 조인.
    - **Hash Cond**: `co.concert_id = c.id`
    - **Index Only Scan on idx_concert_option_concert_id_date_available**: 인덱스 `idx_concert_option_concert_id_date_available`를 사용하여 `concert_option` 테이블을 스캔.
    - **Seq Scan on concert**: `concert` 테이블에 대한 시퀀스 스캔.
    - **실행 시간**: 15.149 ms
    - **처리된 행 수**: 40000 행

### 분석

1. 쿼리 실행 계획의 차이점:
    - 인덱싱 전에는 `concert_option` 테이블에 대한 시퀀스 스캔을 사용하여 모든 행을 스캔하고 해시 조인을 수행합니다.
    - 인덱싱 후에는 `concert_option` 테이블에 대해 인덱스 전용 스캔을 사용하여 필요한 데이터에 빠르게 접근합니다.
2. 성능 차이:
    - **실행 시간**: 인덱싱 전에는 46.836 ms가 소요되었지만, 인덱싱 후에는 15.149 ms로 약 3배 이상 감소하였습니다.
    - **행 처리**: 인덱싱 전후 모두 40000 행을 처리하지만, 인덱싱 후에는 인덱스를 사용하여 더 효율적으로 데이터를 접근합니다.



