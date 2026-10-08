
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import java.net.URL;

public class SmokeTest {
    //tạo điều khiển chung cho các chuc nang eben dưới
    AndroidDriver driver;

    @BeforeEach
    public void setUp() throws Exception {
        //xác định thông tin chô appium
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage("com.swaglabsmobileapp");
        options.setAppActivity("com.swaglabsmobileapp.MainActivity");
        // kết nối với appium server, appium sẽ mở swang labs trên mayys ảo
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
    }

    @Test
    public void kiemTraOTenDangNhap() {
        // tìm và test
        WebDriverWait cho = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement oTenDangNhap = cho.until(
                ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-Username")));
        Assertions.assertTrue(oTenDangNhap.isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        // đóng phiên
        driver.quit();
    }
}