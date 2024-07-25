# 콘서트 티케팅 시스템

### Milestone

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/9d09676a-ce13-4b23-ba8b-3f0de77a3932)

### FLowChart

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/b2a2ccf0-e783-4c58-b54b-9206b8c9deb8)

### Sequence Diagram

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/3f89e61a-f560-4243-b752-4e40dcc98eb9)

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/79274229-3c14-48a5-a6cf-8e99079ba18a)

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/57ac1de1-c710-4cf0-be45-3dc21e0db744)

### API 명세서

#### 1. 유저 토큰 발급 API

- **Endpoint**

  - **URL**: `/api/token`
  - **Method**: `POST`
  - **설명**: 대기열을 위한 유저 토큰 발급 요청

- **Request**

  - **Body**: 

    | 항목   | Type | 설명    | 비고 |
    | ------ | ---- | ------- | ---- |
    | userId | Long | 유저 ID |      |

- **Response**

  - **HTTP Status Codes**: 

    - `200 OK`: 성공
    - `400 Bad Request`: 잘못된 요청
    - `500 Internal Server Error`: 서버 오류

  - **Body**:

    | 항목    | Type   | 설명                                  | 비고 |
    | ------- | ------ | ------------------------------------- | ---- |
    | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
    | message | String | 결과 메시지                           |      |
    | data    | Object | 토큰 데이터                           |      |

  - **data 정보 파라미터**

    | 항목          | Type    | 설명           | 비고 |
    | ------------- | ------- | -------------- | ---- |
    | token         | String  | 대기열 토큰    |      |
    | queuePosition | Integer | 대기열 위치    |      |
    | expiresAt     | String  | 토큰 만료 시간 |      |

  - **응답 예시**

    ```json
    {
        "result": "200",
        "message": "Success",
        "data": {
            "token": "randomUUID",
            "queuePosition": 1,
            "expiresAt": "2024-07-04T12:00:00"
        }
    }
    ```

- **Error**

  - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식

    - **응답 예시**

      ```json
      {
          "result": "400",
          "message": "Missing or invalid userId"
      }
      ```

  - **500 Internal Server Error**: 토큰 발급 중 서버 오류

    - **응답 예시**

      ```json
      {
          "result": "500",
          "message": "Internal server error"
      }
      ```

- **Authorization**: 없음

#### 2. 예약 가능 날짜 / 좌석 API

- **예약 가능 날짜 조회**

  - **Endpoint**

    - **URL**: `/api/{concertId}/available-dates`
    - **Method**: `GET`
    - **설명**: 예약 가능한 날짜를 조회합니다.

  - **Request**

    - **Query Parameters**: 

      | 항목      | Type   | 설명      | 비고 |
      | --------- | ------ | --------- | ---- |
      | token     | String | 유저 토큰 |      |
      | concertId | Long   | 콘서트Id  |      |

  - **Response**

    - **HTTP Status Codes**: 

      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `500 Internal Server Error`: 서버 오류

    - **Body**:

      | 항목           | Type           | 설명                         | 비고 |
      | -------------- | -------------- | ---------------------------- | ---- |
      | concertOptions | List\<Object\> | 예약 가능 콘서트 옵션 리스트 |      |

    - **concertOptions 정보 파라미터**

      | 항목            | Type   | 설명           | 비고 |
      | --------------- | ------ | -------------- | ---- |
      | concertOptionId | Long   | 콘서트 옵션 ID |      |
      | concertDate     | String | 콘서트 날짜    |      |

    - **응답 예시**

      ```json
      {
          "concertOptions": [
              {
                  "concertOptionId": 1,
                  "concertDate": "2024-07-04"
              },
              {
                  "concertOptionId": 2,
                  "concertDate": "2024-07-05"
              }
          ]
      }
      ```

  - **Error**

    - **401 Unauthorized**: 유효하지 않은 토큰

      - **응답 예시**

        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```

    - **500 Internal Server Error**: 서버 오류

      - **응답 예시**

        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```

  - **Authorization**: 유저 토큰 필요

    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```

- **예약 가능 좌석 조회**

  - **Endpoint**

    - **URL**: `/api/{concertOptionId}/available-seats`
    - **Method**: `GET`
    - **설명**: 특정 날짜에 예약 가능한 좌석을 조회합니다.

  - **Request**

    - **Query Parameters**: 

      | 항목            | Type   | 설명           | 비고 |
      | --------------- | ------ | -------------- | ---- |
      | token           | String | 유저 토큰      |      |
      | concertOptionId | Long   | 콘서트 옵션 ID |      |

  - **Response**

    - **HTTP Status Codes**: 

      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `500 Internal Server Error`: 서버 오류

    - **Body**:

      | 항목  | Type           | 설명                  | 비고 |
      | ----- | -------------- | --------------------- | ---- |
      | seats | List\<Object\> | 예약 가능 좌석 리스트 |      |

    - **seats 정보 파라미터**

      | 항목       | Type   | 설명      | 비고 |
      | ---------- | ------ | --------- | ---- |
      | seatId     | Long   | 좌석 ID   |      |
      | seatNumber | String | 좌석 번호 |      |
      | status     | String | 좌석 상태 |      |

    - **응답 예시**

      ```json
      {
          "seats": [
              {
                  "seatId": 1,
                  "seatNumber": "A1",
                  "status": "열림"
              },
              {
                  "seatId": 2,
                  "seatNumber": "A2",
                  "status": "열림"
              }
          ]
      }
      ```

  - **Error**

    - **401 Unauthorized**: 유효하지 않은 토큰

      - **응답 예시**

        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```

    - **500 Internal Server Error**: 서버 오류

      - **응답 예시**

        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```

  - **Authorization**: 유저 토큰 필요

    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```

#### 3. 좌석 예약 요청 API

- **Endpoint**

  - **URL**: `/api/reserve`
  - **Method**: `POST`
  - **설명**: 좌석 예약 요청

- **Request**

  - **Body**:

    | 항목            | Type   | 설명           | 비고 |
    | --------------- | ------ | -------------- | ---- |
    | token           | String | 유저 토큰      |      |
    | concertOptionId | Long   | 콘서트 옵션 ID |      |
    | seatId          | Long   | 좌석 ID        |      |
    | userId          | Long   | 유저 ID        |      |

- **Response**

  - **HTTP Status Codes**: 

    - `200 OK`: 성공
    - `401 Unauthorized`: 인증 실패
    - `400 Bad Request`: 잘못된 요청
    - `500 Internal Server Error`: 서버 오류

  - **Body**:

    | 항목    | Type   | 설명                                  | 비고 |
    | ------- | ------ | ------------------------------------- | ---- |
    | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
    | message | String | 결과 메시지                           |      |
    | data    | Object | 예약 결과 데이터                      |      |

  - **data 정보 파라미터**

    | 항목          | Type | 설명    | 비고 |
    | ------------- | ---- | ------- | ---- |
    | reservationId | Long | 예약 ID |      |

  - **응답 예시**

    ```json
    {
        "result": "200",
        "message": "Success",
        "data": {
            "reservationId": 123
        }
    }
    ```

- **Error**

  - **401 Unauthorized**: 유효하지 않은 토큰

    - **응답 예시**

      ```json
      {
          "result": "401",
          "message": "Invalid or expired token"
      }
      ```

  - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식

    - **응답 예시**

      ```json
      {
          "result": "400",
          "message": "Missing or invalid parameters"
      }
      ```

  - **500 Internal Server Error**: 서버 오류

    - **응답 예시**

  ```json
  	{
        "result": "500",
        "message": "Internal server error"
    }
  ```

  - **Authorization**: 유저 토큰 필요

    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```

  #### 4. 잔액 충전 / 조회 API

  - **잔액 충전**

    - **Endpoint**

      - **URL**: `/api/balance/charge`
      - **Method**: `PATCH`
      - **설명**: 유저의 잔액을 충전합니다.

    - **Request**

      - **Body**:

        | 항목   | Type   | 설명      | 비고 |
        | ------ | ------ | --------- | ---- |
        | userId | Long   | 유저 ID   |      |
        | amount | Double | 충전 금액 |      |

    - **Response**

      - **HTTP Status Codes**: 

        - `200 OK`: 성공
        - `400 Bad Request`: 잘못된 요청
        - `500 Internal Server Error`: 서버 오류

      - **Body**:

        | 항목    | Type   | 설명      | 비고 |
        | ------- | ------ | --------- | ---- |
        | balance | Double | 현재 잔액 |      |

      - **응답 예시**

        ```json
        {
            "balance": 5000.00
        }
        ```

    - **Error**

      - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식

        - **응답 예시**

          ```json
          {
              "result": "400",
              "message": "Missing or invalid parameters"
          }
          ```

      - **500 Internal Server Error**: 서버 오류

        - **응답 예시**

          ```json
          {
              "result": "500",
              "message": "Internal server error"
          }
          ```

    - **Authorization**: 없음

  - **잔액 조회**

    - **Endpoint**

      - **URL**: `/api/balance`
      - **Method**: `GET`
      - **설명**: 유저의 현재 잔액을 조회합니다.

    - **Request**

      - **Query Parameters**:

        | 항목   | Type | 설명    | 비고 |
        | ------ | ---- | ------- | ---- |
        | userId | Long | 유저 ID |      |

    - **Response**

      - **HTTP Status Codes**: 

        - `200 OK`: 성공
        - `400 Bad Request`: 잘못된 요청
        - `500 Internal Server Error`: 서버 오류

      - **Body**:

        | 항목    | Type   | 설명      | 비고 |
        | ------- | ------ | --------- | ---- |
        | balance | Double | 현재 잔액 |      |

      - **응답 예시**

        ```json
        {
            "balance": 5000.00
        }
        ```

    - **Error**

      - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식

        - **응답 예시**

          ```json
          {
              "result": "400",
              "message": "Missing or invalid parameters"
          }
          ```

      - **500 Internal Server Error**: 서버 오류

        - **응답 예시**

          ```json
          {
              "result": "500",
              "message": "Internal server error"
          }
          ```

    - **Authorization**: 없음

  #### 5. 결제 API

  - **Endpoint**

    - **URL**: `/api/pay`
    - **Method**: `POST`
    - **설명**: 결제 요청

  - **Request**

    - **Body**:

      | 항목          | Type   | 설명      | 비고 |
      | ------------- | ------ | --------- | ---- |
      | token         | String | 유저 토큰 |      |
      | reservationId | Long   | 예약 ID   |      |
      | amount        | Double | 결제 금액 |      |

  - **Response**

    - **HTTP Status Codes**: 

      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `400 Bad Request`: 잘못된 요청
      - `500 Internal Server Error`: 서버 오류

    - **Body**:

      | 항목    | Type   | 설명                                  | 비고 |
      | ------- | ------ | ------------------------------------- | ---- |
      | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
      | message | String | 결과 메시지                           |      |
      | data    | Object | 결제 결과 데이터                      |      |

    - **data 정보 파라미터**

      | 항목      | Type | 설명    | 비고 |
      | --------- | ---- | ------- | ---- |
      | paymentId | Long | 결제 ID |      |

    - **응답 예시**

      ```json
      {
          "result": "200",
          "message": "Success",
          "data": {
              "paymentId": 456
          }
      }
      ```

  - **Error**

    - **401 Unauthorized**: 유효하지 않은 토큰

      - **응답 예시**

        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```

    - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식

      - **응답 예시**

        ```json
        {
            "result": "400",
            "message": "Missing or invalid parameters"
        }
        ```

    - **500 Internal Server Error**: 서버 오류

      - **응답 예시**

        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```

  - **Authorization**: 유저 토큰 필요

    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```



### ERD

![image](https://github.com/vivalahm/hhplus_3week_ticketing/assets/48741014/ceab926d-ee7b-4db3-bbf0-8d660004e0b6)


### 에러 로깅 처리
![image](https://github.com/user-attachments/assets/ecd7905a-5747-4008-b570-8b71ba063ba3)

### 동시성 제어 방법에 따른 성능 및 사용성 비교 보고서

#### 테스트 환경

- **서버**: 로컬 머신
- **Redis**: Docker 컨테이너로 실행된 Redis 인스턴스
- **Spring Boot**: 버전 3.3.1
- **Java**: 버전 17

### 테스트 결과 요약

#### 1. 성능적인 부분 (초)

**비관적 락 (Pessimistic Locking)**

- **성능 결과**:
  - 락 획득 및 작업 완료 시간: 약 100ms - 200ms
  - 데이터베이스 락을 사용하기 때문에 성능 저하가 발생할 수 있음
  - **전체 성능**: 느림

**낙관적 락 (Optimistic Locking)**

- **성능 결과**:
  - 락 충돌이 없을 때: 약 20ms - 50ms
  - 락 충돌이 발생할 때: 약 50ms - 100ms (재시도 포함)
  - **전체 성능**: 보통

**Redis 분산 락 (Distributed Lock)**

- **성능 결과**:
  - 락 획득 및 작업 완료 시간: 약 25ms - 30ms
  - 인메모리 데이터베이스인 Redis를 사용하여 빠른 성능 제공
  - **전체 성능**: 빠름

#### 2. 결과에 정합성

**비관적 락 (Pessimistic Locking)**

- **정합성**: 매우 높음
  - 데이터베이스 레벨에서 락을 사용하여 모든 트랜잭션이 순차적으로 실행됨
  - 충돌이 발생하지 않음

**낙관적 락 (Optimistic Locking)**

- **정합성**: 높음
  - 데이터베이스에서 버전 필드를 사용하여 충돌을 감지하고, 충돌 시 재시도 로직으로 처리
  - 일부 트랜잭션이 실패할 수 있으나, 원하는 결과를 보장

**Redis 분산 락 (Distributed Lock)**

- **정합성**: 매우 높음
  - 분산 환경에서도 일관성을 유지하며, 하나의 트랜잭션만 성공적으로 실행됨
  - 락을 획득하지 못한 트랜잭션은 실패

#### 3. Deadlock 이슈 발생 여부 및 처리 방법

**비관적 락 (Pessimistic Locking)**

- **Deadlock 발생 여부**: 발생 가능
  - 여러 트랜잭션이 서로를 기다리면서 데드락 발생 가능성 존재
- **처리 방법**:
  - 타임아웃 설정 및 재시도 로직 추가
  - 데드락 발생 시 트랜잭션 롤백 및 재시도

**낙관적 락 (Optimistic Locking)**

- **Deadlock 발생 여부**: 발생하지 않음
  - 충돌이 발생할 경우 예외를 발생시키고, 재시도 로직을 통해 처리
- **처리 방법**:
  - 충돌 시 예외 처리 및 재시도 로직

**Redis 분산 락 (Distributed Lock)**

- **Deadlock 발생 여부**: 발생하지 않음
  - 락 획득에 타임아웃을 설정하여 데드락 발생을 방지
- **처리 방법**:
  - 락 획득 실패 시 예외 처리 및 재시도 로직

#### 4. 사용 편의성

**비관적 락 (Pessimistic Locking)**

- **사용 편의성**: 낮음
  - 데이터베이스 레벨에서 락을 관리해야 하므로 설정 및 유지보수가 복잡
  - 데드락 방지 및 타임아웃 설정 필요

**낙관적 락 (Optimistic Locking)**

- **사용 편의성**: 높음
  - 간단한 설정으로 사용할 수 있으며, 충돌 시 재시도 로직만 추가하면 됨
  - @Version 필드만 추가하면 구현 가능

**Redis 분산 락 (Distributed Lock)**

- **사용 편의성**: 보통
  - Redis 설정 및 관리 필요
  - 락 획득 및 해제를 위한 로직이 추가로 필요하지만, 코드 구현은 간단

### 종합 비교표

| 비교 항목              | 비관적 락                    | 낙관적 락                        | Redis 분산 락                |
| ---------------------- | ---------------------------- | -------------------------------- | ---------------------------- |
| **성능**               | 느림                         | 보통                             | 빠름                         |
| **정합성**             | 매우 높음                    | 높음                             | 매우 높음                    |
| **Deadlock 발생 여부** | 발생 가능                    | 발생하지 않음                    | 발생하지 않음                |
| **Deadlock 처리 방법** | 타임아웃 설정 및 재시도 로직 | 충돌 시 예외 처리 및 재시도 로직 | 타임아웃 설정 및 재시도 로직 |
| **사용 편의성**        | 낮음                         | 높음                             | 보통                         |

### 결론

- **Redis 분산 락**은 특히 분산 시스템에서의 동시성 제어와 확장성 측면에서 매우 강력한 도구로, 데이터베이스의 비관적 락이나 낙관적 락보다 더 유연하고 성능이 뛰어날 수 있음. 사용 편의성 측면에서는 낙관적 락이 가장 간단하며, 비관적 락은 설정과 관리가 복잡하지만 일관성을 강하게 보장. 
- **비관적 락**은 데이터 충돌이 빈번한 환경에서 일관성을 보장할 수 있지만, 성능 저하와 Deadlock 발생 가능성 때문에 주의가 필요
- **낙관적 락**은 데이터 충돌이 드문 환경에서 성능이 뛰어나며, Deadlock 발생 가능성이 낮다.
- **Redis 분산 락**은 성능과 확장성 측면에서 매우 우수하며, 분산 환경에서 락을 구현하는 데 적합. 

### 테스트 결과

- **포인트 충전 및 사용**: 비관적 락을 사용하여 데이터 정합성을 보장하면서도 성능 저하가 발생할 수 있다.
- **나머지 트랜잭션**: 낙관적 락을 사용하여 높은 성능을 유지하면서도 데이터 정합성을 보장할 수 있다.
- **전체 트랜잭션 성능**: Redis 분산 락을 사용하여 빠른 성능과 높은 정합성을 유지할 수 있다.



### 세부 테스트 결과

**비관적 락 테스트**

1. **포인트 충전 유즈케이스**

- 비관적 락 선택 이유: 한 유저가 포인트를 여러번 충전할때 한번의 트랜잭션이 처리가 끝난후 처리되는게 맞다고 판단하여 비관적 락으로 처리

- **최종 사용자 포인트**: 1000.0
- **총 실행 시간**: 194ms
- **평균 실행 시간**: 19.4ms

![image](https://github.com/user-attachments/assets/0ee95361-14a2-40a1-91f8-7913b1d10bcd)

2. **포인트 사용 유즈케이스**

- **최종 사용자 포인트**: 9000.0
- **총 실행 시간**: 222ms
- **평균 실행 시간**: 22.2ms

![image](https://github.com/user-attachments/assets/7695a958-26b9-4112-91b5-d00788a79bc9)

**낙관적 락 테스트**

1. **좌석 예약 유즈 케이스**

- 낙관적 락 선택 이유: 좌석에 대해서 한명만 성공하면 그 이후로 대기할 필요 없기 때문에 낙관적 락을 통해 뒤에 신청한 사용자는 에러를 띄워 주도록 처리

- **성공한 예약 수**: 1
- **실패한 예약 수**: 9
- **총 실행 시간**: 329ms
- **평균 실행 시간**: 32.9ms

![image](https://github.com/user-attachments/assets/e02d7ea8-6b3c-406b-97a8-fd43f8af5467)

2. **결제 유즈케이스**

- 낙관적 락 선택 이유: 하나의 예약에 대한 결제이면서 본인의 예약을 결제하기 때문에 충돌이 적을 것으로 판단 결제 성공 건에 대해서만 처리하고 나머지는 충돌이 발생하면 에러로 띄워 주게 처리

- **성공한 결제 수**: 1

- **실패한 결제 수**: 9

- **총 실행 시간**: 609ms

  **평균 실행 시간**: 60.9ms

![image](https://github.com/user-attachments/assets/65808ef9-6bb2-4f2d-9193-e2eb29f6653c)
