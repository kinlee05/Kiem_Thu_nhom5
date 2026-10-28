import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tuần 7 - A3: Nhập thông tin thanh toán (CHECKOUT: INFORMATION).
 * Kỹ thuật: sendKeys, clear, hideKeyboard, visibilityOfElementLocated, DataProvider (tương đương parametrize).
 */
public class T7A3_ThanhToanTest extends Tuan07Base {

    private static final By O_HO = id("test-First Name");
    private static final By O_TEN = id("test-Last Name");
    private static final By O_MA_BUU_CHINH = id("test-Zip/Postal Code");
    private static final By NUT_TIEP_TUC = id("test-CONTINUE");
    private static final By NUT_THANH_TOAN = id("test-CHECKOUT");

    /** Chạy sau @BeforeMethod đăng nhập của lớp cha: thêm 1 sản phẩm và vào màn hình nhập thông tin. */
    @BeforeMethod
    public void vaoManHinhThongTin() {
        themSanPhamDauTien();
        bam(ICON_GIO_HANG);
        cuonDenKhiThay(NUT_THANH_TOAN, 4).click();
        doiHienThi(O_HO);
    }

    /** Nhập chữ vào ô; giá trị rỗng nghĩa là bỏ trống ô đó. */
    private void nhap(By o, String giaTri) {
        WebElement oNhap = doiHienThi(o);
        if (!giaTri.isEmpty()) {
            oNhap.sendKeys(giaTri);
        }
    }

    /** Ẩn bàn phím nếu đang hiện (bàn phím che nút CONTINUE). */
    private void anBanPhim() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (WebDriverException ignored) {
            // bàn phím đã ẩn: không sao
        }
    }

    private void dienVaTiepTuc(String ho, String ten, String maBuuChinh) {
        nhap(O_HO, ho);
        nhap(O_TEN, ten);
        nhap(O_MA_BUU_CHINH, maBuuChinh);
        anBanPhim();
        bam(NUT_TIEP_TUC);
    }

    @Test
    public void A3_01_DienDuThongTin() {
        dienVaTiepTuc("An", "Nguyen", "10000");

        Assert.assertTrue(doiHienThi(text("CHECKOUT: OVERVIEW")).isDisplayed(),
                "Điền đủ thông tin phải sang màn hình CHECKOUT: OVERVIEW");
    }

    @DataProvider(name = "thongTinThieu")
    public Object[][] thongTinThieu() {
        return new Object[][] {
                // họ, tên, mã bưu chính, thông báo lỗi mong đợi
                {"", "Nguyen", "10000", "First Name is required"},
                {"An", "", "10000", "Last Name is required"},
                {"An", "Nguyen", "", "Postal Code is required"},
                {"", "", "", "First Name is required"},
        };
    }

    @Test(dataProvider = "thongTinThieu")
    public void A3_02_ThieuThongTinBaoLoi(String ho, String ten, String maBuuChinh, String loiMongDoi) {
        dienVaTiepTuc(ho, ten, maBuuChinh);

        Assert.assertTrue(doiHienThi(text(loiMongDoi)).isDisplayed(), "Thiếu thông báo lỗi: " + loiMongDoi);
        // Vẫn ở màn hình nhập thông tin, không sang bước tiếp theo
        Assert.assertTrue(doiHienThi(O_HO).isDisplayed());
    }

    /** Nhập rồi xoá bằng clear(): ô trở về rỗng nên CONTINUE phải báo thiếu First Name. */
    @Test
    public void A3_03_XoaNoiDungBangClear() {
        nhap(O_HO, "Sai");
        doiHienThi(O_HO).clear();
        nhap(O_TEN, "Nguyen");
        nhap(O_MA_BUU_CHINH, "10000");
        anBanPhim();
        bam(NUT_TIEP_TUC);

        Assert.assertTrue(doiHienThi(text("First Name is required")).isDisplayed(),
                "Sau clear() ô First Name phải rỗng");
    }

    @Test
    public void A3_04_HuyQuayLaiManHinhSanPham() {
        anBanPhim();
        bam(id("test-CANCEL"));

        Assert.assertTrue(
            doiHienThi(id("test-PRODUCTS")).isDisplayed(),
            "Hủy phải quay lại màn hình danh sách sản phẩm");
    }
}