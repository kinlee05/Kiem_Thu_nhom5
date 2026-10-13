import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

class LoginTest {
    AndroidDriver driver;

    @BeforeEach
    public void moApp() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage("com.swaglabsmobileapp");
        options.setAppActivity("com.swaglabsmobileapp.MainActivity");
        // kết nối với appium server, appium sẽ mở swang labs trên mayys ảo
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        //tạo driver, mở app

    }

    @AfterEach
    void dongApp() {
        driver.quit();   // đóng phiên để bài sau không bị ảnh hưởng
    }

    // Hàm dùng chung: gom 3 thao tác lặp lại để khỏi viết 3 lần
    private void dangNhap(String user, String pass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // chờ ô username hiện ra rồi mới gõ
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.accessibilityId("test-Username"))).sendKeys(user);
        driver.findElement(AppiumBy.accessibilityId("test-Password")).sendKeys(pass);
        driver.findElement(AppiumBy.accessibilityId("test-LOGIN")).click();
    }

    @Test
    void dangNhapThanhCong() {
        dangNhap("standard_user", "secret_sauce");
        // Kiểm tra: phần tử đặc trưng của màn Products đang hiển thị
        // assertTrue(driver.findElement(...).isDisplayed());
    }
    @Test
    void dangNhapTaiKhoanKhoa() {
        dangNhap("locked_out_user", "secret_sauce");
        // kiểm tra tài khoản bị khóa với user name bị khóa đã được chotruwocss
    }
    @Test
    void dangNhapThatBai() {
        dangNhap("standard_user", "");
        // kiểm tra với tài khoaản đúng nhưng bỏ trống ô mật khẩu
    }


}