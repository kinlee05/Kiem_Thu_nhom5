# Appium Mobile Testing – Swag Labs (Android)

Kiểm thử tự động ứng dụng Swag Labs bằng Java, Appium, TestNG và Gradle.

| Thư mục | Nội dung |
|---|---|
| `tuan-04/` | Thiết lập môi trường, Smoke Test (`SmokeTest.java`) |
| `tuan-05/` | Kiểm thử Đăng nhập: 3 test case (`LoginTest.java`, `testcases.md`) |
| `tuan-06/` | Bốn bài A1–A4: sắp xếp, giỏ hàng, cuộn/chụp màn hình, phiên Appium + menu |
| `tuan-07/` | Bốn bài A1–A4: chi tiết sản phẩm, trạng thái app + Reset App State, thanh toán, lưới/danh sách |

Mỗi thư mục có `notes.md` (ghi chú tuần) và ảnh minh chứng trong `anh/` (tuần 6: `screenshots/`; tuần 7: `anh/`).

## Chạy kiểm thử
Bật Android Emulator và Appium Server, rồi chạy tại thư mục gốc:

```powershell
.\gradlew.bat clean test                                  # tất cả
.\gradlew.bat test --tests "SmokeTest"                    # tuần 4
.\gradlew.bat test --tests "LoginTest"                    # tuần 5
.\gradlew.bat test --tests "A1_SapXepSanPhamTest"         # tuần 6, từng bài
.\gradlew.bat test --tests "T7*Test"                      # tuần 7
```

Hoặc dùng `run.bat`: `run 7`, `run t1` ... `run t4`.

```text
```
