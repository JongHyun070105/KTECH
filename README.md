# 🍱 KTECH 급식 알리미 Android 앱

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/Glide-4285F4?style=for-the-badge&logo=google&logoColor=white" />
</div>

## 📋 프로젝트 소개

**KTECH 급식 알리미**는 고등학교 학생들을 위한 급식 정보 제공 및 알림 앱입니다.  
사용자는 자신의 학교를 등록하면, 매일 아침과 점심에 오늘의 급식 메뉴를 알림으로 받아볼 수 있습니다.  
또한, 앱 내에서 날짜별 급식 메뉴를 확인할 수 있으며, 공휴일과 주말은 자동으로 제외됩니다.

## ⚙️ 주요 기능

### 🏫 학교 등록 및 로그인
- 사용자는 최초 실행 시 자신의 고등학교 이름을 입력하여 등록합니다.
- 등록된 학교 정보는 SharedPreferences에 저장됩니다.

### 📅 급식 메뉴 조회
- 날짜별로 급식 메뉴를 확인할 수 있습니다.
- 급식 데이터는 `assets/menu.json`에 저장되어 있으며, 앱 설치 시 SQLite DB로 변환됩니다.

### 🔔 급식 알림
- 매일 오전 7시, 오후 12시에 오늘의 급식 메뉴가 알림(Notification)으로 전송됩니다.
- (Android 13 이상은 알림 권한 필요)

### 📅 공휴일/주말 자동 제외
- 공휴일 및 주말에는 급식 알림이 전송되지 않으며, 메뉴도 표시되지 않습니다.

### 📱 위젯 지원
- 급식 정보를 홈 화면 위젯으로도 확인할 수 있습니다.

## 🛠️ 기술 스택

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/kotlin/kotlin-original.svg" alt="kotlin" width="40" height="40"/>
        <br>Kotlin
      </td>
      <td align="center">
        <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/android/android-original.svg" alt="android" width="40" height="40"/>
        <br>Android SDK
      </td>
    </tr>
  </table>
</div>

## 📱 시스템 요구사항

- Android SDK 26 이상
- Kotlin 1.8.20 이상
- Android Studio Hedgehog 이상

## 🔧 설치 방법

1. 프로젝트를 클론합니다:
```bash
git clone https://github.com/JongHyun070105/KTECH.git
```

2. Android Studio에서 프로젝트를 엽니다.
3. 필요한 의존성을 설치합니다:
```bash
./gradlew build
```

4. 앱을 실행합니다.

## 📖 사용 방법
1. 앱을 실행하고 학교 이름을 입력하여 등록합니다.
2. 메인 화면에서 날짜를 선택하여 급식 메뉴를 확인합니다.
3. 알림 설정을 통해 매일 급식 메뉴를 받아볼 수 있습니다.
4. 홈 화면 위젯을 통해 빠르게 급식 정보를 확인할 수 있습니다.

## 📁 프로젝트 구조

```
app/
├── src/
│   └── main/
│        ├── java/com/example/ktech/   # 주요 소스코드
│        ├── res/                      # 레이아웃, 리소스
│        ├── assets/menu.json          # 급식 데이터(JSON)
│        └── AndroidManifest.xml
├── build.gradle
└── ...
```

## 📄 주요 파일 설명

- **MainActivity.java**  
  앱의 메인 화면. 하단 네비게이션과 ViewPager로 홈, 급식, 정보 탭 제공.

- **LoginActivity.java**  
  학교명 입력 및 로그인 처리.

- **MealFragment.java**  
  날짜별 급식 메뉴 표시, 스와이프를 통한 날짜 이동 지원.

- **MealDatabaseHelper.java**  
  급식 데이터(SQLite) 관리 및 공휴일/주말 처리.

- **NotificationReceiver.java**  
  알림 예약 및 전송 로직.

- **menu.json**  
  실제 급식 데이터가 저장된 JSON 파일.

## 📦 사용된 라이브러리

- AndroidX, Material Components
- Glide (이미지 로딩)
- OpenCSV (CSV 파싱)
- SQLite (내장 DB)

## 🔒 권한

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `POST_NOTIFICATIONS` (Android 13 이상)
- `SCHEDULE_EXACT_ALARM`

## 추후 개선 사항
- 급식 데이터 API 호출로 급식 메뉴 불러오기
- 고등학교 인증 API로 처리하기


## 📄 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다.
