import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest {

    // ===== Cách xác định phần tử (kiểm tra lại bằng Appium Inspector) =====
    static final By O_TEN_DANG_NHAP = AppiumBy.accessibilityId("test-Username");
    static final By O_MAT_KHAU      = AppiumBy.accessibilityId("test-Password");
    static final By NUT_LOGIN       = AppiumBy.accessibilityId("test-LOGIN");
    static final By KHUNG_LOI       = AppiumBy.accessibilityId("test-Error message");
    static final By TIEU_DE_PRODUCTS =
            AppiumBy.xpath("//android.widget.TextView[@text='PRODUCTS']");

    AndroidDriver driver;

    @BeforeEach
    void moApp() throws Exception {
        // Tạo phiên mới trước mỗi bài để app luôn bắt đầu ở màn đăng nhập
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity("com.swaglabsmobileapp.MainActivity");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
    }

    @AfterEach
    void dongApp() {
        // Đóng phiên sau mỗi bài để bài sau không bị ảnh hưởng
        if (driver != null) {
            driver.quit();
        }
    }

    // ===== Hàm dùng chung =====

    // Chờ tối đa 10 giây cho tới khi phần tử hiện ra
    private WebElement cho(By cachTim) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(cachTim));
    }

    // Nhập tên, mật khẩu (nếu có) rồi bấm LOGIN
    private void dangNhap(String ten, String matKhau) {
        cho(O_TEN_DANG_NHAP).sendKeys(ten);
        if (!matKhau.isEmpty()) {
            cho(O_MAT_KHAU).sendKeys(matKhau);
        }
        try {
            driver.hideKeyboard();   // bàn phím ảo có thể che nút LOGIN
        } catch (Exception e) {
            // bàn phím không hiện thì bỏ qua
        }
        cho(NUT_LOGIN).click();
    }

    // Lấy chữ của thông báo lỗi (chữ nằm ở phần tử con trong khung lỗi)
    private String layThongBaoLoi() {
        return cho(KHUNG_LOI)
                .findElement(AppiumBy.className("android.widget.TextView"))
                .getText();
    }

    // Còn ở màn đăng nhập hay không (nút LOGIN còn trên màn hình)
    private boolean conOManDangNhap() {
        return !driver.findElements(NUT_LOGIN).isEmpty();
    }

    // ===== Ba bài kiểm thử =====

    @Test
    @DisplayName("TC1 - Đăng nhập thành công vào màn Products")
    void dangNhapThanhCong() {
        dangNhap("standard_user", "secret_sauce");

        // So sánh: tiêu đề màn hình mới phải đúng là "PRODUCTS"
        String tieuDe = cho(TIEU_DE_PRODUCTS).getText();
        assertEquals("PRODUCTS", tieuDe, "Sau khi đăng nhập phải vào màn Products");

        // Màn đăng nhập phải biến mất
        assertTrue(!conOManDangNhap(), "Màn đăng nhập vẫn còn, tức là chưa vào được app");
    }

    @Test
    @DisplayName("TC2 - Tài khoản bị khóa thì báo lỗi")
    void dangNhapTaiKhoanBiKhoa() {
        dangNhap("locked_out_user", "secret_sauce");

        // So sánh: thông báo phải nói tài khoản bị khóa
        String loi = layThongBaoLoi();
        assertTrue(loi.contains("Sorry, this user has been locked out."),
                "Thông báo phải báo tài khoản bị khóa, nhưng app báo: " + loi);

        // Vẫn ở màn đăng nhập, không vào được Products
        assertTrue(conOManDangNhap(), "Tài khoản bị khóa nhưng app đã rời màn đăng nhập");
        assertTrue(driver.findElements(TIEU_DE_PRODUCTS).isEmpty(),
                "Tài khoản bị khóa nhưng vẫn vào được màn Products");
    }

    @Test
    @DisplayName("TC3 - Bỏ trống mật khẩu thì báo Password is required")
    void dangNhapBoTrongMatKhau() {
        dangNhap("standard_user", "");

        // So sánh: thông báo phải đúng như đề yêu cầu
        assertEquals("Password is required", layThongBaoLoi(),
                "Thông báo khi bỏ trống mật khẩu chưa đúng");

        // Vẫn ở màn đăng nhập, không vào được Products
        assertTrue(conOManDangNhap(), "Bỏ trống mật khẩu nhưng app đã rời màn đăng nhập");
        assertTrue(driver.findElements(TIEU_DE_PRODUCTS).isEmpty(),
                "Bỏ trống mật khẩu nhưng vẫn vào được màn Products");
    }
}