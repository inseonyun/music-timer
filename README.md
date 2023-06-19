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

## 개발 목표
+ Android Studio 플랫폼에서 Kotlin 언어를 주로 사용하며, Kotlin 언어에 대한 이해, 개발 경험을 목표로 한다.
+ 알람, 타이머 등의 기능을 구현하여 해당 기능 개발 경험을 목표로 한다.
+ 설정 시간 경과 시 음악을 종료하는 기능을 구현하여, 특정 이벤트(설정 시간 경과) 발생 시 특정 어플(Youtube Music)의 실행을 종료하거나, 이벤트(음악 정지)를 주는 기능 개발 목표를 한다.
---

## 사용 기술
+ Kotlin, Service, Notification
---

## 기능 목록
- 타이머 기능
  - 카운트 다운, 시작, 멈춤 기능
    - 타이머 시간 계산
    - 입력 값 시, 분 공백이어도 동작해야 한다.
  - 타이머 종료 시 음악(미디어)이 종료되어야 한다.
  - Service를 이용한 백그라운드 동작이 가능해야 한다.
  - 타이머가 실행중일 때 Notification에 남은 시간 및 타이머 취소가 가능해야 한다.
  - 타이머 리셋(실행 중이면 Stop 및 Contents 초기화)이 가능해야 한다.


## 발견된 이슈
+ ~ing - 일부 기기 타이머 실행 중 기기 화면 끄기, 방해 금지 모드, 취침 모드 중 타이머 미작동 현상

## 현재 개발 단계
### 앱 화면, UI 
### 타이머 즐겨찾기 기능 추가, 방해 금지 모드에서도 음악 끄기 정상 동작하도록 수정
