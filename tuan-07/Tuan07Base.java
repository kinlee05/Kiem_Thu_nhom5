import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.SkipException;

import java.util.List;
import java.util.Map;

/**
 * Lớp nền dùng chung cho các bài tuần 7 (A1 - A4).
 * Kế thừa BaseTest của tuần 6 (mở phiên, đăng nhập, đóng phiên) và bổ sung locator, hàm tiện ích.
 */
public abstract class Tuan07Base extends BaseTest {

    protected static final String GOI_APP = "com.swaglabsmobileapp";

    // ---------- Locator dùng chung ----------
    protected static final By NUT_THEM = id("test-ADD TO CART");
    protected static final By NUT_XOA = id("test-REMOVE");
    protected static final By SAN_PHAM = id("test-Item");
    protected static final By TEN_SAN_PHAM = id("test-Item title");
    protected static final By GIA_SAN_PHAM = id("test-Price");
    protected static final By MUC_DANG_XUAT = id("test-LOGOUT");
    protected static final By VUNG_CUON = AppiumBy.androidUIAutomator("new UiSelector().scrollable(true)");
    // Số lượng hiển thị trên icon giỏ hàng
    protected static final By HUY_HIEU_GIO =
            By.xpath("//android.view.ViewGroup[@content-desc='test-Cart']//android.widget.TextView");

    // ---------- Hàm tiện ích ----------
    protected void themSanPhamDauTien() {
        doiHienThi(NUT_THEM);
        driver.findElements(NUT_THEM).get(0).click();
    }

    protected void moMenu() {
        bam(NUT_MENU);
        doiHienThi(MUC_DANG_XUAT);
    }

    /** Đóng menu nếu đang mở: ưu tiên nút đóng, không có thì dùng nút Back của Android. */
    protected void dongMenuNeuDangMo() {
        if (driver.findElements(MUC_DANG_XUAT).isEmpty()) {
            return;
        }
        List<WebElement> nutDong = driver.findElements(id("test-Close"));
        if (!nutDong.isEmpty()) {
            nutDong.get(0).click();
        } else {
            driver.navigate().back();
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MUC_DANG_XUAT));
    }

    /**
     * Cuộn xuống từng đoạn bằng mobile: scrollGesture cho tới khi thấy phần tử (tối đa 'toiDa' lần).
     * Nếu màn hình không có vùng cuộn thì dừng ngay.
     */
    protected WebElement cuonDenKhiThay(By by, int toiDa) {
        for (int lan = 0; lan <= toiDa; lan++) {
            List<WebElement> thay = driver.findElements(by);
            if (!thay.isEmpty() && thay.get(0).isDisplayed()) {
                return thay.get(0);
            }
            List<WebElement> vung = driver.findElements(VUNG_CUON);
            if (vung.isEmpty()) {
                break;
            }
            driver.executeScript("mobile: scrollGesture", Map.of(
                    "elementId", ((RemoteWebElement) vung.get(0)).getId(),
                    "direction", "down",
                    "percent", 0.75));
        }
        throw new NoSuchElementException("Cuộn " + toiDa + " lần vẫn không thấy phần tử: " + by);
    }

    /**
     * Tương đương pytest.mark.xfail: test này đang kỳ vọng thất bại vì lỗi đã biết của app.
     * - Kiểm tra thất bại như kỳ vọng  -> test được đánh dấu SKIPPED kèm lý do (XFAIL).
     * - Kiểm tra bất ngờ lại đạt        -> test PASSED và in "XPASS" (lỗi có thể đã được sửa).
     */
    protected void xfail(String lyDo, Runnable kiemTra) {
        try {
            kiemTra.run();
        } catch (AssertionError | TimeoutException e) {
            throw new SkipException("XFAIL (lỗi đã biết): " + lyDo);
        }
        System.out.println("XPASS: kiểm tra đạt, lỗi đã biết có thể đã được sửa: " + lyDo);
    }
}
