# Test case – Chức năng Sắp xếp sản phẩm (A1, tuần 6)

- **Ứng dụng:** Swag Labs (Android), màn PRODUCTS
- **Công cụ:** Appium + UiAutomator2, Java, JUnit 5
- **Tài khoản dùng chung:** `standard_user` / `secret_sauce`
- **File code tương ứng:** `SortProductsTest.java`

**Điều kiện chung cho mọi test case:** máy ảo đang chạy, Appium server đang mở, ứng dụng Swag Labs đã cài; mỗi bài bắt đầu bằng một phiên mới, app ở màn đăng nhập.

**Lưu ý:** app chỉ hiện một số sản phẩm đầu trên màn hình, nên các test case 1 và 2 kiểm tra thứ tự của những sản phẩm đang nhìn thấy. Kiểm tra đủ 6 sản phẩm cần cuộn màn hình (thuộc phần việc của A3).

---

## TC1 – Sắp xếp giá từ thấp đến cao

| Mục | Nội dung |
|---|---|
| Mục đích | Kiểm tra kiểu sắp xếp *Price (low to high)* đưa sản phẩm rẻ nhất lên đầu và giá tăng dần |
| Dữ liệu | Tài khoản `standard_user` / `secret_sauce`; kiểu sắp xếp **Price (low to high)** |

**Các bước:**
1. Mở ứng dụng, nhập `standard_user`, `secret_sauce`, bấm LOGIN.
2. Chờ màn PRODUCTS hiện ra.
3. Bấm nút sắp xếp ở góc phải phía trên danh sách.
4. Chọn **Price (low to high)**.
5. Đọc giá của các sản phẩm đang hiện trên màn hình.

**Kết quả mong đợi:**
- Giá các sản phẩm đang hiện xếp tăng dần từ trên xuống.
- Sản phẩm đầu tiên là sản phẩm rẻ nhất, giá **$7.99**.

---

## TC2 – Sắp xếp tên từ Z đến A

| Mục | Nội dung |
|---|---|
| Mục đích | Kiểm tra kiểu sắp xếp *Name (Z to A)* xếp tên sản phẩm giảm dần theo bảng chữ cái |
| Dữ liệu | Tài khoản `standard_user` / `secret_sauce`; kiểu sắp xếp **Name (Z to A)** |

**Các bước:**
1. Mở ứng dụng, đăng nhập bằng `standard_user` / `secret_sauce`.
2. Chờ màn PRODUCTS hiện ra.
3. Bấm nút sắp xếp.
4. Chọn **Name (Z to A)**.
5. Đọc tên của các sản phẩm đang hiện trên màn hình.

**Kết quả mong đợi:**
- Tên các sản phẩm đang hiện xếp từ Z đến A (giảm dần) từ trên xuống.

---

## TC3 – Chờ phần tử không tồn tại thì báo TimeoutException (trường hợp sai)

| Mục | Nội dung |
|---|---|
| Mục đích | Kiểm tra chờ tường minh báo `TimeoutException` khi phần tử không xuất hiện (mục 5.1 Explicit Waits, 7.1 Exceptions) |
| Dữ liệu | Tài khoản `standard_user` / `secret_sauce`; phần tử không có trên màn hình: accessibility id `test-khong-ton-tai`; thời gian chờ 3 giây |

**Các bước:**
1. Mở ứng dụng, đăng nhập bằng `standard_user` / `secret_sauce`, chờ vào màn PRODUCTS.
2. Chờ phần tử có accessibility id `test-khong-ton-tai` hiện ra, tối đa 3 giây.

**Kết quả mong đợi:**
- Sau 3 giây, thư viện báo `TimeoutException`.
- Test pass khi exception này xảy ra, và fail nếu không có exception nào.

---

## TC4 – Chờ ngầm định: tìm phần tử không có thì vẫn đợi hết thời gian

| Mục | Nội dung |
|---|---|
| Mục đích | Kiểm tra `implicitlyWait` làm `findElements` đợi trước khi trả về danh sách rỗng (mục 5.2 Implicit Waits) |
| Dữ liệu | Tài khoản `standard_user` / `secret_sauce`; chờ ngầm định 5 giây; phần tử không có: `test-khong-ton-tai` |

**Các bước:**
1. Mở ứng dụng, đăng nhập bằng `standard_user` / `secret_sauce`, chờ vào màn PRODUCTS.
2. Đặt chờ ngầm định 5 giây.
3. Dùng `findElements` tìm phần tử có accessibility id `test-khong-ton-tai` và đo thời gian lệnh chạy.
4. Tắt chờ ngầm định (đặt lại bằng 0).

**Kết quả mong đợi:**
- `findElements` không báo lỗi mà trả về danh sách **rỗng**.
- Lệnh chạy mất khoảng 5 giây (ít nhất 4 giây) trước khi trả về.