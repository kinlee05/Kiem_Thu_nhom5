Môi trường đã dùng
Thành phần	Phiên bản
JDK	17
Node.js	bản LTS
Appium server	3.8.0
Driver UiAutomator2	8.7.0
Appium java-client	9.3.0
Selenium	4.23.0
JUnit	5.10.2
Công cụ	Android Studio, IntelliJ IDEA
Ứng dụng kiểm thử	Swag Labs Sample App (Android) 2.7.1
Hướng dẫn cài đặt
JDK 17: cài xong, đặt biến môi trường JAVA_HOME và thêm %JAVA_HOME%\bin vào Path. Kiểm tra: java -version.
Node.js (LTS): tải tại nodejs.org. Kiểm tra: node -v.
Appium và driver:
bash
   npm install -g appium
   appium driver install uiautomator2
   appium driver list --installed
Android Studio: cài cùng Android SDK. Đặt biến môi trường ANDROID_HOME trỏ tới thư mục SDK, thêm %ANDROID_HOME%\platform-tools và %ANDROID_HOME%\emulator vào Path. Kiểm tra: adb version.
Tạo máy ảo: Android Studio, Device Manager, Create Virtual Device (ví dụ Pixel, một system image Android bản mới).
Tải file .apk Android của Swag Labs tại trang Releases của repo saucelabs/sample-app-mobile (bản 2.7.1). Đặt vào thư mục có đường dẫn không dấu, không khoảng trắng.
Cài ứng dụng vào máy ảo

Bật máy ảo, đợi tới màn hình chính, rồi:

bash
adb devices
adb install "<duong-dan>\Android.SauceLabs.Mobile.Sample.app.2.7.1.apk"

adb devices phải thấy emulator-5554   device, adb install phải báo Success.

Thông tin ứng dụng dùng trong test:

appPackage: com.swaglabsmobileapp
appActivity: com.swaglabsmobileapp.MainActivity
Cách chạy bài kiểm thử
Bật máy ảo, mở sẵn ứng dụng Swag Labs ở màn hình đăng nhập (hoặc để Appium tự mở).
Mở một cửa sổ CMD riêng và chạy Appium server, để mở suốt trong lúc test:
bash
   appium
Mở thư mục tuan-04/appium-smoke bằng IntelliJ IDEA (chọn JDK 17), đợi Maven tải thư viện.
Mở src/test/java/SmokeTest.java, bấm mũi tên xanh cạnh @Test và chọn Run.

Nếu máy có cài Maven, có thể chạy bằng dòng lệnh trong thư mục appium-smoke:

bash
mvn test

Kết quả mong đợi: ứng dụng tự mở trên máy ảo, cửa sổ Run báo 1 test passed, exit code 0.
