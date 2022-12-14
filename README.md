## Music Timer App - 음악 끄기 타이머 앱

## 구글 Play Store 2022. 08. 16. 출시
+ [[Play Store 바로가기]](https://play.google.com/store/apps/details?id=com.media_music_timer)

## 대표 이미지
<p orientaion=horizontal align=center>
  <img src=img/[2022.08.21]app-img/app-main.jpg height=500>
  <img src=img/[2022.08.21]app-img/app-main-timer-processing.jpg height=500>
  <br>
  <img src=img/[2022.08.21]app-img/app-main-notification.jpg height=500>
  <img src=img/[2022.08.21]app-img/app-main-notification-extension.jpg height=500>
  <img src=img/app-img/music-stop.gif height=500>
</p>

## 개요
+ 안드로이드 - Galaxy 휴대폰 환경에서 'Youtube Music' 사용 중 불편함을 느낀 음악 끄기 타이머 기능이 앱에도, One UI 상에도 존재하지 않아 고안하게 되었다.
+ 이 앱은 사용자가 지정한 시간이 경과되면 자동으로 음악을 종료한다.
---

## 사용 기술
+ Kotlin
---

## 개발 목표
+ Android Studio 플랫폼에서 Kotlin 언어를 주로 사용하며, Kotlin 언어에 대한 이해, 개발 경험을 목표로 한다.
+ 알람, 타이머 등의 기능을 구현하여 해당 기능 개발 경험을 목표로 한다.
+ 설정 시간 경과 시 음악을 종료하는 기능을 구현하여, 특정 이벤트(설정 시간 경과) 발생 시 특정 어플(Youtube Music)의 실행을 종료하거나, 이벤트(음악 정지)를 주는 기능 개발 목표를 한다.
---

## 개발 현황
  ### 1. 개발
    1. 타이머 기능 (카운트 다운, 시작, 멈춤) 구현
    2. 타이머 설정 예외 처리 구현
    3. 타이머 시간 계산(밀리 초 -> 초, 분, 시) 구현
    4. 타이머 실행 시간 퍼센트 구하여 프로그래스바로 표현
    5. 타이머 종료 시 음악 종료 기능 구현
    6. 타이머 멈춘 후 00분 00초로 재개 했을 때 타이머가 시작되는 현상 해결
    7. Service를 이용하여 백그라운드에서 타이머 기능 수행하도록 함
    8. SDK 26버전 이상부터 백그라운드에서 서비스 수행 시 Notification이 필요하여 구현
    9. SDK 31버전 이상부터 Notification의 PaddingIntent Flag 값 조정 및 관련 라이브러리 implementation 
    10. 타이머 실행중일 때 Notification에 남은 시간 구현
    11. 타이머 실행중일 때 Notification 확장 시 타이머 취소 구현
    12. 타이머 리셋 기능(Notification 실행 중이면 stop, 각 contents 초기화) 구현
    13. 입력 값 시, 분 중 하나만 공백이어도 타이머 정상 동작하도록 함
    
  ### 2. 디자인
    1. 써클 프로그래스바 구현
    2. 라운드 버튼 구현
    3. 앱 테마 색상 설정 및 사용자 컬러 추가
    4. 프로그레스바 진행 시 뚝뚝 끊기는게 아닌 애니메이션으로 동작하도록 변경
    5. 앱 이미지 제작 및 반영
    6. 메인 컬러 추가 및 강조, EditText 색상 변경
    7. 테마 EditText 포커싱 밑줄 색상 변경
    8. Notification용 아이콘 생성
    9. Notification 알림창에 남은 타이머 시간 표시 및 타이머 취소 버튼 표시
    10. Action Bar에 타이머 리셋 버튼 추가

## 발견된 이슈
+ 2022.08.03. 발생 - 타이머 실행 중 테마 모드(라이트 -> 다크 혹은 다크 -> 라이트) 변경 시 앱이 초기화 됨 
  + 2022.08.07. 해결 - [[바로가기]](https://github.com/inseonyun/music-timer/pull/2)
  
+ ~ing - 일부 기기 타이머 실행 중 기기 화면 끄기, 방해 금지 모드, 취침 모드 중 타이머 미작동 현상

## 현재 개발 단계
### 앱 화면, UI 
### 타이머 즐겨찾기 기능 추가, 방해 금지 모드에서도 음악 끄기 정상 동작하도록 수정
