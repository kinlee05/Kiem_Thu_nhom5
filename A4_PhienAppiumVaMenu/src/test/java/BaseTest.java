import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.time.Duration;

/**
 * Lớp nền dùng chung cho các bài tuần 6 (A1 - A4).
 * Mỗi test: mở phiên Appium mới -> đăng nhập -> chạy test -> đóng phiên.
 */
public abstract class BaseTest {

    protected static final String APPIUM_URL = "http://127.0.0.1:4723";

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    // ---------- Locator dùng chung (kiểm tra lại bằng Appium Inspector nếu app khác bản) ----------
    protected static final By O_USERNAME   = id("test-Username");
    protected static final By O_PASSWORD   = id("test-Password");
    protected static final By NUT_LOGIN    = id("test-LOGIN");
    protected static final By MAN_HINH_PRODUCTS = text("PRODUCTS");
    protected static final By ICON_GIO_HANG = id("test-Cart");
    protected static final By NUT_MENU     = id("test-Menu");

    protected static By id(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    protected static By text(String noiDung) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + noiDung + "\")");
    }

    // ---------- Tạo phiên Appium (Remote WebDriver) ----------
    protected static AndroidDriver taoDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity(".SplashActivity");
        options.setCapability("appium:forceAppLaunch", true);
        // Tắt hẳn app nếu đang chạy trước khi mở phiên mới, tránh dính trạng thái của test trước (vd menu đang mở)
        options.setCapability("appium:shouldTerminateApp", true);
        options.setNewCommandTimeout(Duration.ofSeconds(120));

        AndroidDriver d = new AndroidDriver(URI.create(APPIUM_URL).toURL(), options);
        // Chỉ dùng explicit wait là chính, nên để implicit wait = 0 (không trộn hai loại wait)
        d.manage().timeouts().implicitlyWait(Duration.ZERO);
        return d;
    }

    @BeforeMethod(alwaysRun = true)
    public void khoiDong() throws Exception {
        driver = taoDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        dangNhap("standard_user", "secret_sauce");
    }

    @AfterMethod(alwaysRun = true)
    public void dongPhien() {
        if (driver != null) {
            try {
                driver.terminateApp("com.swaglabsmobileapp"); // đóng app để test sau bắt đầu sạch
            } catch (Exception ignored) {
                // không quan trọng: vẫn đóng phiên bên dưới
            }
            driver.quit();
            driver = null;
        }
    }

    // ---------- Hàm tiện ích ----------
    protected void dangNhap(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(O_USERNAME)).sendKeys(username);
        driver.findElement(O_PASSWORD).sendKeys(password);
        driver.findElement(NUT_LOGIN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAN_HINH_PRODUCTS));
    }

    protected WebElement doiHienThi(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void bam(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }
}