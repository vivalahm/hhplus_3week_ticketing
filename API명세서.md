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