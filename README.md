# 너나들이

<a href="https://neonaduri.com"> <img alt="너나들이" src="https://user-images.githubusercontent.com/100130070/171794135-f0527617-b95b-47c6-8b1a-117b073c5e99.png" width="550" height="350"> </a>

<br>

# 🥨 Project

### 프로젝트 설명

<pre>“실시간 여행계획플랜 서비스! ”

너나들이는, 함께 여행을 가고싶은 사람들과 
실시간으로 계획을 작성하고 공유할 수 있는 서비스입니다 :)</pre>


## 📌 페이지별 기능

- **`홈`**
  사람들이 공유한 여행 플랜을 구경하고, 원하는 여행플랜을 스크랩 할 수 있습니다.
  행사 및 테마&인가&지역별 여행플랜으로 구성되어있어요 :)
  <br>
- **`계획`**
  여행을 함께 갈 친구들과 같이 지도를 보면서 계획을 짜보세요.
  계획저장은 방장만 가능해요 ~!
  <br>
- **`검색`**
  원하는 여행 키워드를 검색하여, 여행플랜을 찾아볼 수 있습니다.
  최신순&조회순&찜 많은 순 으로 나눠서 볼 수 있어요!
  <br>
- **`후기`**
  여행을 직접 다녀온 유저들의 생생하고 솔직한 후기를 작성하고 공유할 수 있습니다.  
  <br>
- **`마이페이지`**
  프로필&비밀번호 변경, 내가 등록한 후기 및 게시글, 스크랩한 여행 플랜을 볼 수 있으며
  회원탈퇴 기능도 있습니다.

## [🏷 API Table 🏷](https://fuchsia-guardian-8bc.notion.site/ed2c26a96a034d5bb70fba989f6b6a3f?v=10575f3fec604347a877af7dfd36cb27)


# 👩‍💻 개발기간 및 팀원소개 👨‍💻
## 기간: 2022.04.22. ~ 2022.06.03.


|  이름   |                           깃허브 주소                            | 포지션 |
|:-----:|:--------------------------------------------------------------: | :----: |
| 오예령🔰 |  [https://github.com/ohyeryung](https://github.com/ohyeryung)  | 백엔드 |
|  백승호  |   [https://github.com/SEUNGHO1216](https://github.com/SEUNGHO1216)    | 백엔드 |
|  김지호  | [https://github.com/Zoe-Jiho-Kim](https://github.com/Zoe-Jiho-Kim) | 백엔드 |

<br>

# 🔨 기술 스택

|     이름     |        설명        |
|:----------:|:----------------:|
|  AWS EC2   |     서버 인스턴스      |
| SpringBoot |     스프링부트 사용     |
|   MySQL    |     데이터 베이스      |
|    git     |      버전 관리       |


<br>

## [🏷 API Table 🏷](https://fuchsia-guardian-8bc.notion.site/ed2c26a96a034d5bb70fba989f6b6a3f?v=10575f3fec604347a877af7dfd36cb27)


## 아키텍처✨

<img src="https://user-images.githubusercontent.com/89297158/171176309-a0918a08-0596-43da-810e-e1b9737e98d0.png"/>

## ERD

<img src="https://user-images.githubusercontent.com/89297158/171332401-36a1c418-717f-4271-bc75-8199f05dc0c3.png" />

<br>

## 사이트 데모🎥

<details>

<summary>데모영상</summary>

|메인페이지|계획세우기|실시간 기능| 
|:---:|:---:|:---:| 
|<img src="https://user-images.githubusercontent.com/89297158/171185593-a9a75922-30e1-4c4b-854c-27bc61d9ce40.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171189957-4dc456e4-4a72-48a8-a9ca-5fd68037d558.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171189232-4fc38d69-ded8-4e67-a39e-dcf211a6433b.gif" width="250"/>|
|검색(무한스크롤)|상세페이지|댓글|
|<img src="https://user-images.githubusercontent.com/89297158/171191609-19a1e6e8-f755-441a-99cc-2b70923e5843.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171192659-be36af5c-429a-4e7a-92a4-4d66fdb02b12.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171195102-6c9f3639-c061-4db2-8ef8-a4076f9280ef.gif" width="250"/>|
|회원가입|로그인|마이페이지|
|<img src="https://user-images.githubusercontent.com/89297158/171347275-8a500aef-240d-40c1-957c-9bcb3b804e6b.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171183020-e364b78b-1aec-4f0f-9481-0348554f1066.gif" width="250"/>|<img src="https://user-images.githubusercontent.com/89297158/171401291-539ca8f1-5fb1-42c7-abe2-a6dd09eebb76.gif" width="250"/>|
</details>

<br>


## 📢 User Test

<details markdown="2">
<summary>개선사항 사례</summary>

#### ✏️ "실시간으로 공유되는 부분을 모르겠어요,,,"

> 여행계획 세우는 페이지 내, 각 장소에 따른 메모 작성시
>
> 해당 textarea를 활성화

#### ✏️ "검색기능에 필터링 추가해주세요! "

> 최신순을 default로 설정하고, 조회수 순, 찜 순 3가지로 필터링기능 추가

</details>

<br>

### ⚙️ 개발 환경
- **Server** : AWS EC2(Ubuntu 20.04 LTS)
- **Framework** : Springboot
- **Database** : Mysql (AWS RDS)
- **ETC** : AWS S3, AWS Cloudfront, AWS LoadBalancer, AWS ROUTE 53, AWS IAM
