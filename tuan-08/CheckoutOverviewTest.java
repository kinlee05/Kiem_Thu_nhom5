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

import java.math.BigDecimal;
import java.net.URL;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// Chức năng 3 (team A3): Tổng kết đơn hàng và hoàn tất
class CheckoutOverviewTest {

    // ===== Cách xác định phần tử =====
    // Màn đăng nhập
    static final By O_TEN_DANG_NHAP = AppiumBy.accessibilityId("test-Username");
    static final By O_MAT_KHAU      = AppiumBy.accessibilityId("test-Password");
    static final By NUT_LOGIN       = AppiumBy.accessibilityId("test-LOGIN");
    // Màn Products và giỏ hàng
    static final By TIEU_DE_PRODUCTS = AppiumBy.xpath("//android.widget.TextView[@text='PRODUCTS']");
    static final By TEN_SAN_PHAM     = AppiumBy.accessibilityId("test-Item title");
    static final By GIA_SAN_PHAM     = AppiumBy.xpath("//android.widget.TextView[starts-with(@text,'$')]");
    static final By NUT_THEM_GIO     = AppiumBy.accessibilityId("test-ADD TO CART");
    static final By ICON_GIO_HANG    = AppiumBy.accessibilityId("test-Cart");
    static final By NUT_CHECKOUT     = AppiumBy.accessibilityId("test-CHECKOUT");
    // Màn nhập thông tin
    static final By O_HO       = AppiumBy.accessibilityId("test-First Name");
    static final By O_TEN      = AppiumBy.accessibilityId("test-Last Name");
    static final By O_ZIP      = AppiumBy.accessibilityId("test-Zip/Postal Code");
    static final By NUT_CONTINUE = AppiumBy.accessibilityId("test-CONTINUE");
    // Màn tổng kết (Overview) và hoàn tất
    static final By TIEU_DE_OVERVIEW = AppiumBy.xpath("//android.widget.TextView[@text='CHECKOUT: OVERVIEW']");
    static final By NUT_FINISH       = AppiumBy.accessibilityId("test-FINISH");
    static final By NUT_CANCEL       = AppiumBy.accessibilityId("test-CANCEL");
    static final By TIEU_DE_COMPLETE = AppiumBy.xpath("//android.widget.TextView[@text='CHECKOUT: COMPLETE!']");
    static final By CHU_CAM_ON       = AppiumBy.xpath("//android.widget.TextView[@text='THANK YOU FOR YOU ORDER']");

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

    // Cuộn màn hình cho tới khi thấy phần tử có accessibility id (content-desc) này
    private WebElement cuonToiMoTa(String moTa) {
        return cho(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().description(\"" + moTa + "\"))"));
    }

    // Cuộn màn hình cho tới khi thấy phần tử có chữ bắt đầu bằng đoạn này
    private WebElement cuonToiChu(String dauChu) {
        return cho(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().textStartsWith(\"" + dauChu + "\"))"));
    }

    private void nhap(By o, String chu) {
        WebElement phanTu = cho(o);
        phanTu.click();
        phanTu.sendKeys(chu);
    }

    private void anBanPhim() {
        try {
            driver.hideKeyboard();   // bàn phím ảo có thể che nút CONTINUE
        } catch (Exception e) {
            // bàn phím không hiện thì bỏ qua
        }
    }

    // Lấy số tiền trong chuỗi như "Item total: $29.99" -> 29.99 (dùng BigDecimal để không bị sai số)
    private BigDecimal layTien(String chuoi) {
        Matcher m = Pattern.compile("\\$\\s*(\\d+(?:\\.\\d+)?)").matcher(chuoi);
        if (!m.find()) {
            fail("Không tìm thấy số tiền trong chuỗi: " + chuoi);
        }
        return new BigDecimal(m.group(1));
    }

    private void dangNhap() {
        cho(O_TEN_DANG_NHAP).sendKeys("standard_user");
        cho(O_MAT_KHAU).sendKeys("secret_sauce");
        anBanPhim();
        cho(NUT_LOGIN).click();
        cho(TIEU_DE_PRODUCTS);
    }

    // Từ màn Products: thêm sản phẩm đầu tiên, đi qua giỏ hàng và màn nhập thông tin tới màn Overview
    private void diToiManTongKet() {
        cho(NUT_THEM_GIO).click();
        cho(ICON_GIO_HANG).click();
        cuonToiMoTa("test-CHECKOUT").click();
        nhap(O_HO, "Kien");
        nhap(O_TEN, "Lee");
        nhap(O_ZIP, "100000");
        anBanPhim();
        cho(NUT_CONTINUE).click();
        cho(TIEU_DE_OVERVIEW);   // chờ tới khi vào đúng màn tổng kết
    }

    // ===== Ba bài kiểm thử =====

    @Test
    @DisplayName("TC1 - Hoàn tất đơn hàng thì hiện màn cảm ơn")
    void hoanTatDonHang() {
        dangNhap();
        diToiManTongKet();

        cuonToiMoTa("test-FINISH").click();

        // So sánh: phải vào màn hoàn tất và có lời cảm ơn
        assertEquals("CHECKOUT: COMPLETE!", cho(TIEU_DE_COMPLETE).getText(),
                "Sau khi bấm FINISH phải vào màn hoàn tất");
        assertEquals("THANK YOU FOR YOU ORDER", cho(CHU_CAM_ON).getText(),
                "Màn hoàn tất phải có lời cảm ơn");
        assertTrue(driver.findElements(TIEU_DE_OVERVIEW).isEmpty(),
                "Màn tổng kết vẫn còn, tức là đơn chưa hoàn tất");
    }

    @Test
    @DisplayName("TC2 - Màn tổng kết hiển thị đúng tên, giá và tổng tiền")
    void manTongKetDungSoTien() {
        dangNhap();

        // Ghi lại tên và giá sản phẩm đầu tiên ở danh sách
        String tenTrenDanhSach = cho(TEN_SAN_PHAM).getText();
        BigDecimal giaTrenDanhSach = layTien(driver.findElements(GIA_SAN_PHAM).get(0).getText());

        diToiManTongKet();

        // So sánh tên và giá ở Overview với danh sách
        assertEquals(tenTrenDanhSach, cho(TEN_SAN_PHAM).getText(), "Tên sản phẩm ở Overview khác danh sách");
        BigDecimal giaOverview = layTien(driver.findElements(GIA_SAN_PHAM).get(0).getText());
        assertEquals(0, giaTrenDanhSach.compareTo(giaOverview), "Giá ở Overview khác danh sách");

        // Cuộn xuống để thấy phần tiền
        BigDecimal tongHang = layTien(cuonToiChu("Item total").getText());
        BigDecimal thue     = layTien(cuonToiChu("Tax").getText());
        BigDecimal tong     = layTien(cuonToiChu("Total").getText());

        assertEquals(0, giaTrenDanhSach.compareTo(tongHang), "Item total phải bằng giá 1 sản phẩm");
        assertEquals(0, tongHang.add(thue).compareTo(tong),
                "Total phải bằng Item total + Tax: " + tongHang + " + " + thue + " khác " + tong);
    }

    @Test
    @DisplayName("TC3 - Bấm CANCEL ở màn tổng kết thì quay về Products, chưa đặt hàng")
    void huyOManTongKet() {
        dangNhap();
        diToiManTongKet();

        cuonToiMoTa("test-CANCEL").click();

        // So sánh: quay về màn Products, không vào màn hoàn tất
        assertEquals("PRODUCTS", cho(TIEU_DE_PRODUCTS).getText(), "Bấm CANCEL phải quay về màn Products");
        assertTrue(driver.findElements(TIEU_DE_COMPLETE).isEmpty(), "Chưa bấm FINISH mà đã hoàn tất đơn");
    }
}