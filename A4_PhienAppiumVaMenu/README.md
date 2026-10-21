# A4 – Phiên Appium và Menu (Maven)

Dự án Maven độc lập cho bài A4, tách khỏi các bài khác trong `tuan-06`.

## Công nghệ
- Java 17+
- Maven
- Appium Java Client 9.3.0
- TestNG 7.10.2

## Điều kiện trước khi chạy
1. Cài JDK 17 trở lên và Maven.
2. Cài Node.js, Appium Server và driver UiAutomator2.
3. Khởi động Android Emulator hoặc thiết bị Android đã được cấu hình để dùng với Appium.
4. Theo cấu hình hiện tại, thiết bị phải có tên `emulator-5554`, ứng dụng Swag Labs phải được cài với package `com.swaglabsmobileapp`, activity `.SplashActivity`.
5. Khởi động Appium Server tại `http://127.0.0.1:4723`.

## Chạy bài A4
Mở terminal tại thư mục có `pom.xml`, chạy:

```bash
mvn test
```

Surefire được cấu hình để chạy `A4_PhienAppiumVaMenuTest`.

## Lưu ý
- `mvn test` sẽ tải các thư viện Maven cần thiết ở lần chạy đầu tiên.
- Maven không tự cài JDK, Appium Server, driver UiAutomator2, emulator hoặc ứng dụng Swag Labs.
- Nếu máy khác dùng địa chỉ Appium hoặc tên thiết bị khác, sửa `APPIUM_URL` và `setDeviceName(...)` trong `BaseTest.java`.
