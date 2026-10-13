# test case chức năng đăng nhập - Swag labs
## test case 1 - đăng nhập thành công:
- các bước : mở app-> nhập tên đăng nhập-> nhập mật khẩu-> bấm LOGIN
- Dữ liệu nhập vào : standard_user/ secret_sauce
- Kết quả mong đợi: chuyển sang màn hình products

## test case 2 - tài khoản bị khóa
- các bước : mở app-> nhập tên đăng nhập-> nhập mật khẩu-> bấm LOGIN
- dữ liệu : locked_out_user/ secret_sauce
- kết quả mong đợi: ở lại màn đăng nhập, hiện thông báo tài khoản bị khóa.

## test case 3 - bbor trống mật khẩu 
- các bước : mở app-> nhập tên đăng nhập-> không nhập mật khẩu-> bấm LOGIN
- dữ liệu : standard_user/ (để trống)
- kể quả mong đợi : hiện thông báo "password is required"
