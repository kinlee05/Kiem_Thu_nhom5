import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortProductsTest {


    // Màn đăng nhập
    static final By O_TEN_DANG_NHAP = AppiumBy.accessibilityId("test-Username");
    static final By O_MAT_KHAU      = AppiumBy.accessibilityId("test-Password");
    static final By NUT_LOGIN       = AppiumBy.accessibilityId("test-LOGIN");
    static final By TIEU_DE_PRODUCTS =
            AppiumBy.xpath("//android.widget.TextView[@text='PRODUCTS']");

    // Màn Products
    static final By NUT_SAP_XEP  = AppiumBy.accessibilityId("test-Modal Selector Button");
    static final By TEN_SAN_PHAM = AppiumBy.accessibilityId("test-Item title");
    static final By GIA_SAN_PHAM = AppiumBy.accessibilityId("test-Price");

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

    // Chờ tối đa 10 giây cho tới khi phần tử hiện ra (chờ tường minh)
    private WebElement cho(By cachTim) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(cachTim));
    }

    // Chờ tối đa 10 giây cho tới khi phần tử bấm được rồi mới bấm
    private void choRoiBam(By cachTim) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(cachTim))
                .click();
    }

    // Đăng nhập bằng tài khoản chuẩn và chờ vào được màn Products
    private void dangNhapVaoProducts() {
        cho(O_TEN_DANG_NHAP).sendKeys("standard_user");
        cho(O_MAT_KHAU).sendKeys("secret_sauce");
        try {
            driver.hideKeyboard();   // bàn phím ảo có thể che nút LOGIN
        } catch (Exception e) {
            // bàn phím không hiện thì bỏ qua
        }
        cho(NUT_LOGIN).click();
        cho(TIEU_DE_PRODUCTS);       // chờ màn Products hiện ra
    }

    // Bấm nút sắp xếp rồi chọn một kiểu, ví dụ "Price (low to high)"
    private void chonKieuSapXep(String nhan) {
        choRoiBam(NUT_SAP_XEP);
        By luaChon = AppiumBy.xpath("//android.widget.TextView[@text='" + nhan + "']");
        choRoiBam(luaChon);
        cho(GIA_SAN_PHAM);           // chờ danh sách sản phẩm hiện lại sau khi sắp xếp
    }

    // Lấy giá các sản phẩm đang hiện trên màn hình, đổi "$7.99" thành số 7.99
    private List<Double> layDanhSachGia() {
        List<Double> ketQua = new ArrayList<>();
        for (WebElement phanTu : driver.findElements(GIA_SAN_PHAM)) {
            ketQua.add(Double.parseDouble(phanTu.getText().replace("$", "").trim()));
        }
        return ketQua;
    }

    // Lấy tên các sản phẩm đang hiện trên màn hình
    private List<String> layDanhSachTen() {
        List<String> ketQua = new ArrayList<>();
        for (WebElement phanTu : driver.findElements(TEN_SAN_PHAM)) {
            ketQua.add(phanTu.getText());
        }
        return ketQua;
    }

    // ===== Các bài kiểm thử =====
    // Lưu ý: app chỉ vẽ các sản phẩm đang hiện trên màn hình, nên các bài dưới đây
    // kiểm tra thứ tự của những sản phẩm nhìn thấy (kiểm tra đủ 6 sản phẩm cần cuộn màn hình).

    @Test
    @DisplayName("TC1 - Sắp xếp giá từ thấp đến cao, sản phẩm đầu là rẻ nhất")
    void sapXepGiaTangDan() {
        dangNhapVaoProducts();
        chonKieuSapXep("Price (low to high)");

        List<Double> gia = layDanhSachGia();
        assertTrue(gia.size() >= 2, "Cần ít nhất 2 sản phẩm hiện ra để so sánh, nhưng chỉ có " + gia.size());

        // So sánh: danh sách giá trên màn hình phải trùng với danh sách giá đã sắp xếp tăng dần
        List<Double> giaDaSapXep = new ArrayList<>(gia);
        Collections.sort(giaDaSapXep);
        assertEquals(giaDaSapXep, gia, "Giá chưa tăng dần: " + gia);

        // Sản phẩm rẻ nhất của app là $7.99 nên phải nằm đầu danh sách
        assertEquals(7.99, gia.get(0), 0.001, "Sản phẩm đầu tiên phải là sản phẩm rẻ nhất");
    }

    @Test
    @DisplayName("TC2 - Sắp xếp tên từ Z đến A")
    void sapXepTenZDenA() {
        dangNhapVaoProducts();
        chonKieuSapXep("Name (Z to A)");

        List<String> ten = layDanhSachTen();
        assertTrue(ten.size() >= 2, "Cần ít nhất 2 sản phẩm hiện ra để so sánh, nhưng chỉ có " + ten.size());

        // So sánh: danh sách tên trên màn hình phải trùng với danh sách đã sắp xếp giảm dần
        List<String> tenDaSapXep = new ArrayList<>(ten);
        tenDaSapXep.sort(Collections.reverseOrder());
        assertEquals(tenDaSapXep, ten, "Tên chưa xếp từ Z đến A: " + ten);
    }

    @Test
    @DisplayName("TC3 - Chờ phần tử không tồn tại thì báo TimeoutException")
    void choPhanTuKhongTonTaiBaoTimeout() {
        dangNhapVaoProducts();

        // Trường hợp sai: tìm một phần tử không có trên màn hình, chỉ chờ 3 giây
        By khongTonTai = AppiumBy.accessibilityId("test-khong-ton-tai");
        assertThrows(TimeoutException.class, () ->
                        new WebDriverWait(driver, Duration.ofSeconds(3))
                                .until(ExpectedConditions.visibilityOfElementLocated(khongTonTai)),
                "Chờ phần tử không tồn tại thì phải báo TimeoutException");
    }

    @Test
    @DisplayName("TC4 - Chờ ngầm định: findElements không có kết quả vẫn phải đợi hết thời gian")
    void choNgamDinh() {
        dangNhapVaoProducts();

        // Bật chờ ngầm định 5 giây: mọi lệnh tìm phần tử đều đợi tối đa 5 giây
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        try {
            long batDau = System.currentTimeMillis();
            List<WebElement> ketQua = driver.findElements(AppiumBy.accessibilityId("test-khong-ton-tai"));
            long thoiGianCho = System.currentTimeMillis() - batDau;

            // findElements không báo lỗi mà trả về danh sách rỗng, nhưng phải mất ~5 giây mới trả về
            assertTrue(ketQua.isEmpty(), "Phần tử không tồn tại thì danh sách phải rỗng");
            assertTrue(thoiGianCho >= 4000,
                    "Chờ ngầm định phải đợi gần 5 giây, nhưng chỉ đợi " + thoiGianCho + " ms");
        } finally {
            // Tắt chờ ngầm định để không trộn với chờ tường minh
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        }
    }
}