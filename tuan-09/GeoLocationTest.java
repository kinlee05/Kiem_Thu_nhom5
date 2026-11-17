import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoLocationTest {

    // ===== Cách xác định phần tử (phần đăng nhập giữ nguyên như LoginTest) =====
    static final By O_TEN_DANG_NHAP  = AppiumBy.accessibilityId("test-Username");
    static final By O_MAT_KHAU       = AppiumBy.accessibilityId("test-Password");
    static final By NUT_LOGIN        = AppiumBy.accessibilityId("test-LOGIN");
    static final By TIEU_DE_PRODUCTS =
            AppiumBy.xpath("//android.widget.TextView[@text='PRODUCTS']");

    // Các phần tử của menu - CẦN KIỂM TRA LẠI bằng Appium Inspector
    static final By NUT_MENU         = AppiumBy.accessibilityId("test-Menu");
    static final By MUC_GEO_LOCATION = AppiumBy.accessibilityId("test-GEO LOCATION");
    static final By MAN_GEO_LOCATION =
            AppiumBy.xpath("//android.widget.TextView[contains(@text,'GEO')]");

    // ===== Vị trí giả lập =====
    static final double HA_NOI_VI_DO = 21.0285, HA_NOI_KINH_DO = 105.8542;
    static final double HCM_VI_DO    = 10.8231, HCM_KINH_DO    = 106.6297;
    static final double SAI_SO       = 0.01;   // cho phép lệch nhẹ khi đọc lại toạ độ

    AndroidDriver driver;

    @BeforeEach
    void moApp() throws Exception {
        // Tạo phiên mới trước mỗi bài để app luôn bắt đầu ở màn đăng nhập
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity("com.swaglabsmobileapp.MainActivity")
                .setAutoGrantPermissions(true);   // tự cấp quyền vị trí cho app
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);

        // Đăng nhập rồi mở màn GEO LOCATION để các bài bắt đầu từ đó
        dangNhap("standard_user", "secret_sauce");
        cho(TIEU_DE_PRODUCTS);
        moManGeoLocation();
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

    // Nhập tên, mật khẩu rồi bấm LOGIN
    private void dangNhap(String ten, String matKhau) {
        cho(O_TEN_DANG_NHAP).sendKeys(ten);
        cho(O_MAT_KHAU).sendKeys(matKhau);
        try {
            driver.hideKeyboard();   // bàn phím ảo có thể che nút LOGIN
        } catch (Exception e) {
            // bàn phím không hiện thì bỏ qua
        }
        cho(NUT_LOGIN).click();
    }

    // Mở menu rồi chọn GEO LOCATION
    private void moManGeoLocation() {
        cho(NUT_MENU).click();
        cho(MUC_GEO_LOCATION).click();
        chapNhanHopThoaiQuyenNeuCo();
        cho(MAN_GEO_LOCATION);
    }

    // Nếu hệ thống hiện hộp thoại xin quyền vị trí thì bấm cho phép
    private void chapNhanHopThoaiQuyenNeuCo() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException e) {
            // không có hộp thoại (đã tự cấp quyền) thì bỏ qua
        }
    }

    // Đặt vị trí giả lập cho thiết bị (lệnh của UiAutomator2)
    private void datViTri(double viDo, double kinhDo) {
        driver.executeScript("mobile: setGeolocation",
                Map.of("latitude", viDo, "longitude", kinhDo));
    }

    // Đọc vị trí hiện tại của thiết bị, trả về {vĩ độ, kinh độ}
    private double[] docViTri() {
        Map<?, ?> kq = (Map<?, ?>) driver.executeScript("mobile: getGeolocation");
        double viDo = Double.parseDouble(String.valueOf(kq.get("latitude")));
        double kinhDo = Double.parseDouble(String.valueOf(kq.get("longitude")));
        return new double[]{viDo, kinhDo};
    }

    // Đặt vị trí rồi chờ (tối đa 15 giây) cho tới khi máy báo đúng vị trí đó.
    // Trả về vị trí đọc được ở lần cuối, để bài test so sánh và báo lỗi rõ ràng.
    private double[] datViTriRoiDoc(double viDo, double kinhDo) {
        datViTri(viDo, kinhDo);
        double[] ketQua = new double[]{0, 0};
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .pollingEvery(Duration.ofSeconds(1))
                    .until(d -> {
                        try {
                            double[] v = docViTri();
                            ketQua[0] = v[0];
                            ketQua[1] = v[1];
                            return Math.abs(v[0] - viDo) < SAI_SO
                                    && Math.abs(v[1] - kinhDo) < SAI_SO;
                        } catch (Exception e) {
                            return false;   // chưa đọc được thì chờ lần sau
                        }
                    });
        } catch (TimeoutException e) {
            // hết giờ vẫn chưa khớp: để bài test tự so sánh và báo lỗi
        }
        return ketQua;
    }

    // ===== Ba bài kiểm thử =====

    @Test
    @DisplayName("GEO1 - Đặt vị trí giả lập Hà Nội thì đọc lại đúng toạ độ")
    void datViTriHaNoi() {
        // Đặt vị trí Hà Nội và chờ máy cập nhật
        double[] viTri = datViTriRoiDoc(HA_NOI_VI_DO, HA_NOI_KINH_DO);

        // So sánh: toạ độ đọc lại phải đúng là Hà Nội
        assertEquals(HA_NOI_VI_DO, viTri[0], SAI_SO, "Vĩ độ chưa đúng Hà Nội");
        assertEquals(HA_NOI_KINH_DO, viTri[1], SAI_SO, "Kinh độ chưa đúng Hà Nội");
    }

    @Test
    @DisplayName("GEO2 - Đổi sang TP.HCM thì toạ độ thay đổi theo")
    void doiSangViTriKhac() {
        double viDoCu = datViTriRoiDoc(HA_NOI_VI_DO, HA_NOI_KINH_DO)[0];

        double[] viTriMoi = datViTriRoiDoc(HCM_VI_DO, HCM_KINH_DO);

        // So sánh: vị trí phải khác lúc trước và đúng là TP.HCM
        assertNotEquals(viDoCu, viTriMoi[0], SAI_SO, "Đã đổi vị trí nhưng toạ độ không đổi");
        assertEquals(HCM_VI_DO, viTriMoi[0], SAI_SO, "Vĩ độ chưa đúng TP.HCM");
        assertEquals(HCM_KINH_DO, viTriMoi[1], SAI_SO, "Kinh độ chưa đúng TP.HCM");
    }

    @Test
    @DisplayName("GEO3 - Toạ độ không hợp lệ (vĩ độ 200) thì bị từ chối")
    void toaDoKhongHopLe() {
        // Trường hợp sai: vĩ độ chỉ được từ -90 đến 90, nên đặt 200 phải báo lỗi
        assertThrows(WebDriverException.class,
                () -> datViTri(200.0, HA_NOI_KINH_DO),
                "Vĩ độ 200 không hợp lệ nhưng driver không báo lỗi");
    }
}