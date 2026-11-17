
import time
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoAlertPresentException

# 1. Cấu hình Desired Capabilities
options = UiAutomator2Options()
options.platform_name = "Android"
options.automation_name = "UiAutomator2"
options.device_name = "emulator-5554"
options.app_package = "com.swaglabsmobileapp"
options.app_activity = "com.swaglabsmobileapp.MainActivity"

# Đường dẫn file APK
options.app = "/Users/trong10112002/Downloads/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk"

# Tự động cấp quyền ứng dụng
options.auto_grant_permissions = True

# 2. Khởi tạo kết nối Appium
driver = webdriver.Remote("http://127.0.0.1:4723", options=options)

# Khởi tạo Explicit Wait
wait = WebDriverWait(driver, 15)

try:
    # 3. Kiểm tra và xử lý Alert nếu xuất hiện
    try:
        alert = WebDriverWait(driver, 3).until(
            EC.alert_is_present()
        )
        alert.accept()
        print("Đã chấp nhận Alert!")

    except NoAlertPresentException:
        print("Không có Alert xuất hiện.")

    except Exception:
        print("Không có Alert cần xử lý.")

    # Cũng sử dụng switch_to.alert để xử lý Alert
    try:
        driver.switch_to.alert.accept()
        print("Đã xử lý Alert bằng switch_to.alert!")

    except NoAlertPresentException:
        print("Không tìm thấy Alert.")

    # 4. Đăng nhập vào Swag Labs
    user_field = wait.until(
        EC.presence_of_element_located(
            (AppiumBy.ACCESSIBILITY_ID, "test-Username")
        )
    )
    user_field.send_keys("standard_user")

    pass_field = driver.find_element(
        AppiumBy.ACCESSIBILITY_ID, "test-Password"
    )
    pass_field.send_keys("secret_sauce")

    login_btn = driver.find_element(
        AppiumBy.ACCESSIBILITY_ID, "test-LOGIN"
    )
    login_btn.click()

    # 5. Giả lập vị trí GPS bằng set_location
    target_lat = 21.0285
    target_lon = 105.8542
    target_alt = 10.0

    driver.set_location(
        target_lat,
        target_lon,
        target_alt
    )

    print(f"Đã giả lập tọa độ: Lat={target_lat}, Lon={target_lon}")

    # 6. Lấy vị trí hiện tại bằng location
    current_location = driver.location

    print("Vị trí hiện tại của thiết bị:")
    print(f"Latitude: {current_location['latitude']}")
    print(f"Longitude: {current_location['longitude']}")
    print(f"Altitude: {current_location['altitude']}")

    # 7. Mở Menu
    menu_btn = wait.until(
        EC.element_to_be_clickable(
            (AppiumBy.ACCESSIBILITY_ID, "test-Menu")
        )
    )
    menu_btn.click()

    # 8. Chọn GEO LOCATION
    geo_menu_item = wait.until(
        EC.element_to_be_clickable(
            (AppiumBy.ACCESSIBILITY_ID, "test-GEO LOCATION")
        )
    )
    geo_menu_item.click()

    # 9. Lấy tọa độ hiển thị trên ứng dụng bằng text
    lat_element = wait.until(
        EC.presence_of_element_located(
            (AppiumBy.ACCESSIBILITY_ID, "test-latitude")
        )
    )

    lon_element = wait.until(
        EC.presence_of_element_located(
            (AppiumBy.ACCESSIBILITY_ID, "test-longitude")
        )
    )

    displayed_lat = lat_element.text
    displayed_lon = lon_element.text

    print("Tọa độ hiển thị trên App:")
    print(f"Latitude = {displayed_lat}")
    print(f"Longitude = {displayed_lon}")

    # 10. Kiểm tra kết quả
    assert str(target_lat)[:5] in displayed_lat, \
        f"Sai Latitude! Mong đợi {target_lat}, thực tế {displayed_lat}"

    assert str(target_lon)[:6] in displayed_lon, \
        f"Sai Longitude! Mong đợi {target_lon}, thực tế {displayed_lon}"

    print("TEST PASSED: Giả lập vị trí thành công!")

except Exception as e:
    print(f"TEST FAILED: {e}")

finally:
    time.sleep(3)
    driver.quit()
