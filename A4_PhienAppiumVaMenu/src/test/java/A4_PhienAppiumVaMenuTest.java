import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

/**
 * A4 - Phiên Appium (Remote WebDriver) và menu.
 * Kỹ thuật: new AndroidDriver(url, options), getSessionId, capabilities, navigate().back().
 * Mỗi test: mở phiên Appium mới -> đăng nhập -> chạy test -> đóng phiên.
 */
public class A4_PhienAppiumVaMenuTest {

    private static final String APPIUM_URL = "http://127.0.0.1:4723";

    private AndroidDriver driver;
    private WebDriverWait wait;

    // ---------- Locator (kiểm tra lại bằng Appium Inspector nếu app khác bản) ----------
    private static final By O_USERNAME = id("test-Username");
    private static final By O_PASSWORD = id("test-Password");
    private static final By NUT_LOGIN = id("test-LOGIN");
    private static final By MAN_HINH_PRODUCTS = text("PRODUCTS");
    private static final By ICON_GIO_HANG = id("test-Cart");
    private static final By NUT_MENU = id("test-Menu");

    private static By id(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    private static By text(String noiDung) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + noiDung + "\")");
    }

    // ---------- Tạo phiên Appium (Remote WebDriver) ----------
    private static AndroidDriver taoDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity(".SplashActivity");
        options.setCapability("appium:forceAppLaunch", true);
        // Tắt hẳn app nếu đang chạy trước khi mở phiên mới, tránh dính trạng thái test trước (vd menu đang mở)
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
    private void dangNhap(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(O_USERNAME)).sendKeys(username);
        driver.findElement(O_PASSWORD).sendKeys(password);
        driver.findElement(NUT_LOGIN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAN_HINH_PRODUCTS));
    }

    private WebElement doiHienThi(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private void bam(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    // ================== CÁC BÀI KIỂM THỬ ==================

    @Test
    public void A4_01_ThongTinPhienVaCapabilities() {
        Assert.assertNotNull(driver.getSessionId(), "Phiên chưa có session id");
        System.out.println("A4_01 session id: " + driver.getSessionId());
        System.out.println("A4_01 capabilities: " + driver.getCapabilities().asMap());

        // Session id phải đúng dạng UUID do Appium server cấp (ném lỗi nếu sai định dạng)
        UUID.fromString(driver.getSessionId().toString());

        // Capabilities server trả về phải khớp với những gì taoDriver() đã yêu cầu
        Capabilities caps = driver.getCapabilities();
        String nenTang = String.valueOf(caps.getCapability("platformName"));
        Assert.assertTrue(nenTang.toLowerCase().contains("android"),
                "platformName không phải Android: " + nenTang);
        Assert.assertEquals(String.valueOf(caps.getCapability("appium:automationName")), "UIAutomator2");
        Assert.assertEquals(String.valueOf(caps.getCapability("appium:appPackage")), "com.swaglabsmobileapp");
        Assert.assertEquals(String.valueOf(caps.getCapability("appium:deviceName")), "emulator-5554");

        // Bằng chứng phiên đang điều khiển máy thật: hỏi thẳng máy ảo app nào đang mở
        Assert.assertEquals(driver.getCurrentPackage(), "com.swaglabsmobileapp",
                "Máy ảo không đang chạy Swag Labs");
    }

    /** Đóng phiên cũ, mở phiên mới: session id khác nhau, phiên cũ chết, phiên mới dùng được. */
    @Test
    public void A4_02_MoiPhienCoSessionIdRieng() throws Exception {
        AndroidDriver phienCu = driver;
        String idCu = phienCu.getSessionId().toString();
        phienCu.quit();

        // Phiên cũ đã đóng: gọi lệnh vào nó phải lỗi
        Assert.assertThrows(WebDriverException.class, phienCu::getPageSource);

        AndroidDriver phienMoi = taoDriver();
        driver = phienMoi; // để dongPhien() đóng phiên mới
        wait = new WebDriverWait(phienMoi, Duration.ofSeconds(15));
        String idMoi = phienMoi.getSessionId().toString();
        Assert.assertNotEquals(idMoi, idCu, "Hai phiên không được trùng session id");

        // Phiên mới phải điều khiển được app: đăng nhập thành công
        dangNhap("standard_user", "secret_sauce");
        Assert.assertTrue(doiHienThi(MAN_HINH_PRODUCTS).isDisplayed(), "Phiên mới không đăng nhập được");
    }

    @Test
    public void A4_03_MoMenu() {
        bam(NUT_MENU);
        Assert.assertTrue(doiHienThi(id("test-ALL ITEMS")).isDisplayed(), "Thiếu mục ALL ITEMS");
        Assert.assertTrue(doiHienThi(id("test-LOGOUT")).isDisplayed(), "Thiếu mục LOGOUT");
    }

    @Test
    public void A4_04_DangXuatQuaMenu() {
        bam(NUT_MENU);
        bam(id("test-LOGOUT"));
        Assert.assertTrue(doiHienThi(O_USERNAME).isDisplayed(),
                "Đăng xuất xong phải quay lại màn hình đăng nhập");
    }

    /** Vào giỏ hàng rồi nhấn Back (nút quay lại của Android): phải về lại màn hình Products. */
    @Test
    public void A4_05_NutBack() {
        bam(ICON_GIO_HANG);
        doiHienThi(id("test-CONTINUE SHOPPING"));

        driver.navigate().back();

        wait.until(ExpectedConditions.visibilityOfElementLocated(MAN_HINH_PRODUCTS));
    }
}