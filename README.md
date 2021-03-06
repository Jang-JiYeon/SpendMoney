# SpendMoney
> 카카오페이에는 머니 뿌리기 기능 구현
* 사용자는 다수의 친구들이 있는 대화방에서 뿌릴 금액과 받아갈 대상의 숫자를 입력하여 뿌리기 요청을 보낼 수 있습니다.
* 요청 시 자신의 잔액이 감소되고 대화방에는 뿌리기 메세지가 발송됩니다.
* 대화방에 있는 다른 사용자들은 위에 발송된 메세지를 클릭하여 금액을 무작위로 받아가게 됩니다.

## 개발 환경

> **기본 환경**
* Eclipse
* Window10
* Git

> **Server**
* Java8
* Spring Boot 2.3.1
* Mybatis
* H2 DB
* Gradle

## 실행
* Eclipse STS 사용하지 않을 경우(사용할 경우 프로젝트 우클릭해서 실행)   
  SpendMoneyApplication.java 우클릭 -> Run As -> Java Application
* build.gradle 우클릭 -> Gradle -> Refresh Gradle Project
* http://localhost/spendMoney_db 접속해서 로그인 후 DB 테이블 생성   
  - 계정 정보 : application.yml   
  - DB 테이블 : /SpendMoney/src/main/resources/spendMoney_db.txt

# 핵심 문제해결 전략
## 개발 제약사항
1. 개발 언어는 Java, kotlin, scala 중 익숙한 개발 언어를 선택하여 과제를 진행
2. 핵심 문제해결 전략을 간단하게 작성하여 readme.md 파일에 첨부
3. 데이터베이스 사용에는 제약이 없음
4. API 의 HTTP Method들 (GET | POST | PUT | DEL) 은 자유롭게 선택
5. 에러응답, 에러코드는 자유롭게 정의

* 성공/실패 응답 경우 SpendMoneyApiResponse 객체생성하여 동일하게 JSON 결과값 주도록함.
* 로그로 sql문 확인하기 위해 log4j 설정

## 상세 요건

> **공통사항**
1. 뿌리기, 받기, 조회기능을 REST API로 구현   
  1-1. 요청한 사용자의 식별값은 숫자형태 이며 "X-USER-ID"라는 HTTP Header로 전달   
  1-2. 요청한 사용자가 속한 대화방의 식별값은 문자형태이며 "X-ROOM-ID"라는 HTTP Header로 전달   
  1-3. 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않음.   
2. 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없어야함.   
3. 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성.   

* X-USER-ID와 X-ROOM-ID는 고유한 UID를 사용한다는 가정하에 32크기로 설계.
* X-USER-ID는 숫자, X-ROOM-ID는 문자 형태임을 api 호출시마다 체크

> **뿌리기 API**
1. 뿌릴금액, 뿌릴 인원을 요청값으로 받음
2. 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줌
3. 뿌릴 금액을 인원수에 맞게 분배하여 저장
4. token은 3자리 문자열로 구성되며 예측이 불가능해야 한다.  

* 뿌릴 금액과 뿌릴 인원이 파라미터로 전달되었는지 유무 체크
* token은 Random함수를 사용하여 직접 숫자,소문자,대문자를 랜덤으로 생성
* 인원수에 맞게 분배
  - 인원수만큼 반복문 실행   
  - 처음은 총액의 범위 안에서 랜덤한 금액 생성   
  - 랜덤한 금액 생성시 분배할 금액에서 계속 차감
  - 분배되고 남은 돈 범위 안에서 랜덤한 금액 생성 반복
* 모두 금액이 분배되면 DB에 저장 후 응답값에 token 값 세팅

> **받기 API**
1. 뿌리기 시 발급된 token을 요청값으로 받음
2. token에 해당하는 뿌리기 건 중   
   아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고,   
   그 금액을 응답값으로 내려줌 
3. 뿌리기 당 한 사용자는 한번만 받을 수 있음 
4. 자신이 뿌리기한 건은 자신이 받을 수 없음.
5. 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있음. 
6. 뿌린 건은 10분간만 유효. 10분이 지난 요청은 받기 실패 응답이 내려져야함.   

* token이 파라미터로 전달되었는지 유무 체크
* DB 조회 및 저장
  - 받기 요청자가 뿌리기 한 요청자인지 체크
  - 받기 요청자가 이미 받은 적이 있는지 체크
  - 받기 요청자가 뿌리기 요청자의 대화방과 동일한지 체크 후,  
    데이터 가져와서 10분이 지난 요청건인지 체크
  - 각 체크에서 걸리는 것이 하나라도 있다면 오류코드 전달
  - 체크과정에서 걸리는 것이 없다면, 분배 금액 응답값에 저장 후 받기 완료된 데이터 저장   

> **조회 API**
1. 뿌리기 시 발급된 token을 요청값으로 받음
2. token에 해당하는 뿌리기 건의 현재 상태를 3번 응답값으로 내려줌.
3. 뿌린 시간, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보([받은금액, 받은 사용자 아이디]리스트)
4. 뿌린 사람 자신만 조회 할 수 있음.   
   다름사람의 뿌리기 건은이나 유효하지 않은 token은 조회 실패 응답.
5. 뿌린 건에 대한 조회는 7일동안 가능

* token이 파라미터로 전달되었는지 유무 체크
* DB 조회
  - 조회 요청자가 뿌리기 요청한 본인인지 체크
  - 본인이라면 7일 지난 건인지 체크
  - 각 체크에서 걸리는 것이 하나라도 있다면 오류코드 전달
  - 체크과정에서 걸리는 것이 없다면, 뿌리기 건의 현재 상태 응닶값으로 전달

## 응답 코드
Code | Message | Used
---|---|---
0000 | SUCCESS | 공통
0001 | Header값을 확인 바랍니다. | 공통
0002 | 대화방ID는 문자만 가능하며, 요청자ID 숫자만 요청 가능합니다. | 공통
0003 | 인원과 금액을 입력바랍니다. | 뿌리기 API
0004 | 인원과 금액은 숫자만 입력 가능합니다. | 뿌리기 API   
0005 | 토큰값을 입력바랍니다. | 받기,조회 API
0006 | 조회 실패하였습니다. | 조회 API
0007 | 7일 지난 건은 조회 불가입니다. | 조회 API
0008 | 자신이 뿌리기한 건은 자신이 받을 수 없습니다. | 받기 API
0009 | 뿌리기 당 사용자는 한번만 받을 수 있습니다.  | 받기 API 
0010 | 받을 수 있는 금액이 없습니다. | 받기 API 
0011 | 뿌린 후 10분이 지난 요청은 받을 수 없습니다. | 받기 API  

## API 목록
Method | Path | 설명   
---|---|---
POST | /api/v1/sendMoney    |  돈 뿌리기 기능   
POST | /api/v1/receiveMoney |  돈 받기 기능   
POST | /api/v1/searchMoney  |  뿌린 금액 조회    

## 공통 요청 헤더
Key	    |   	  Value	              |              설명   
---|---|---
X-USER-ID	| 12345678910123456789101234567891 | 사용자 아이디   
X-ROOM-ID	| qwertyuiopasdfghjklzxcvbnmqwer  | 대화방 식별키   

## 돈 뿌리기 API
/api/v1/sendMoney

* 요청
<pre><code>
{
    "sendPsersonNum" : "4",
    "sendTotalMoney" : "10000"
}
</code></pre>

* 응답
<pre><code>
{
  "Result": {
    "code": "0000",
    "message": "SUCCESS",
    "data": "6eh"
  }
}
</code></pre>

## 돈 받기 API
/api/v1/receiveMoney

* 요청
<pre><code>
{
    "token" : "6eh"
}
</code></pre>

* 응답
<pre><code>
{
  "Result": {
    "code": "0000",
    "message": "SUCCESS",
    "data": "7613"
  }
}
</code></pre>

## 금액 조회 API
/api/v1/searchMoney

* 요청
<pre><code>
{
    "token" : "O5U"
}
</code></pre>

* 응답
<pre><code>
{
  "Result": {
    "code": "0000",
    "message": "SUCCESS",
    "data": {
      "receiveData": [
        {
          "divMoney": "1615",
          "receiveId": "5432487"
        },
        {
          "divMoney": "4413",
          "receiveId": "123456789"
        },
        {
          "divMoney": "23972",
          "receiveId": "7984654564213"
        }
      ],
      "sendTime": "2020-06-27 02:37:52.115",
      "sendMoney": "30000",
      "receiveMoney": 30000
    }
  }
}
</code></pre>
