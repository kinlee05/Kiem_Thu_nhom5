import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.UUID;

/**
 * A4 - Phiên Appium (Remote WebDriver) và menu.
 * Kỹ thuật: new AndroidDriver(url, options), getSessionId, capabilities, navigate().back().
 */
public class A4_PhienAppiumVaMenuTest extends BaseTest {

    @Test
    public void A4_01_ThongTinPhienVaCapabilities() {
        Assert.assertNotNull(driver.getSessionId(), "Phiên chưa có session id");
        System.out.println("A4_01 session id: " + driver.getSessionId());
        System.out.println("A4_01 capabilities: " + driver.getCapabilities().asMap());

        // Session id phải đúng dạng UUID do Appium server cấp (ném lỗi nếu sai định dạng)
        UUID.fromString(driver.getSessionId().toString());

        // Capabilities server trả về phải khớp với những gì BaseTest đã yêu cầu
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
        driver = phienMoi; // để @AfterMethod đóng phiên mới
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