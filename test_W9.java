
import io.appium.java_client.AppiumBy;
import io.appium.java_client.Location;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

public class TestGeoLocation {

    public static void main(String[] args) {
        AndroidDriver driver = null;

        try {
            // 1. Cau hinh Desired Capabilities
            UiAutomator2Options options = new UiAutomator2Options();

            options.setPlatformName("Android");
            options.setAutomationName("UiAutomator2");
            options.setDeviceName("emulator-5554");
            options.setAppPackage("com.swaglabsmobileapp");
            options.setAppActivity("com.swaglabsmobileapp.MainActivity");

            options.setApp(
                "/Users/trong10112002/Downloads/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk"
            );

            // Tu dong cap quyen
            options.setAutoGrantPermissions(true);

            // 2. Ket noi Appium Server
            driver = new AndroidDriver(
                new URL("http://127.0.0.1:4723"),
                options
            );

            WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(15)
            );

            // 3. Xu ly Alert neu xuat hien
            try {
                new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.alertIsPresent());

                driver.switchTo().alert().accept();
                System.out.println("Da chap nhan Alert!");

            } catch (TimeoutException | NoAlertPresentException e) {
                System.out.println("Khong co Alert can xu ly.");
            }

            // 4. Dang nhap Swag Labs
            WebElement userField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.accessibilityId("test-Username")
                )
            );
            userField.sendKeys("standard_user");

            WebElement passField = driver.findElement(
                AppiumBy.accessibilityId("test-Password")
            );
            passField.sendKeys("secret_sauce");

            WebElement loginButton = driver.findElement(
                AppiumBy.accessibilityId("test-LOGIN")
            );
            loginButton.click();

            // 5. Gia lap vi tri GPS
            double targetLat = 21.0285;
            double targetLon = 105.8542;
            double targetAlt = 10.0;

            Location targetLocation = new Location(
                targetLat, targetLon, targetAlt
            );

            driver.setLocation(targetLocation);

            System.out.println("Da gia lap toa do: "
                + targetLat + ", " + targetLon);

            // 6. Lay vi tri hien tai
            Location currentLocation = driver.getLocation();

            System.out.println("Vi tri hien tai:");
            System.out.println("Latitude: " + currentLocation.getLatitude());
            System.out.println("Longitude: " + currentLocation.getLongitude());
            System.out.println("Altitude: " + currentLocation.getAltitude());

            // 7. Mo Menu
            WebElement menuButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("test-Menu")
                )
            );
            menuButton.click();

            // 8. Chon GEO LOCATION
            WebElement geoMenuItem = wait.until(
                ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("test-GEO LOCATION")
                )
            );
            geoMenuItem.click();

            // 9. Lay toa do bang getText()
            WebElement latElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.accessibilityId("test-latitude")
                )
            );

            WebElement lonElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.accessibilityId("test-longitude")
                )
            );

            String displayedLat = latElement.getText();
            String displayedLon = lonElement.getText();

            System.out.println("Latitude = " + displayedLat);
            System.out.println("Longitude = " + displayedLon);

            // 10. Kiem tra ket qua
            if (!displayedLat.contains(
                String.valueOf(targetLat).substring(0, 5))) {
                throw new AssertionError("Sai Latitude: " + displayedLat);
            }

            if (!displayedLon.contains(
                String.valueOf(targetLon).substring(0, 6))) {
                throw new AssertionError("Sai Longitude: " + displayedLon);
            }

            System.out.println(
                "TEST PASSED: Gia lap vi tri thanh cong!"
            );

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (driver != null) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                driver.quit();
            }
        }
    }
}
