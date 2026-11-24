
package Selenium;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WorkTrackProject1 {

    WebDriver driver;
    WebDriverWait wait;

    String userEmail = "anbukkarasisethu32@gmail.com";
    String userPassword = "12345678";
    String url = "https://employee-worktrack-5lmu.vercel.app/login";

    @BeforeClass
    public void setUp() {

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.get(url);

        System.out.println("WorkTrack Website Opened");
    }

    // LOGIN METHOD
    private void ensureLoggedIn() {

        if (driver.getCurrentUrl().contains("/login")) {

            WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='email']")
                )
            );

            WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='password']")
                )
            );

            email.clear();
            email.sendKeys(userEmail);

            password.clear();
            password.sendKeys(userPassword);

            WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(translate(normalize-space(.),"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
                    + "'login')]")
                )
            );

            loginButton.click();

            wait.until(ExpectedConditions.urlContains("/dashboard"));

            System.out.println("Login Successful");
        }
    }

    // MENU CLICK METHOD
    private void clickMenu(String menuName) {

        ensureLoggedIn();

        String wanted = menuName.toLowerCase(Locale.ROOT).trim();

        By locator = By.xpath(
            "//*[contains(translate(normalize-space(.),"
            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
            + "'" + wanted + "')]"
        );

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        List<WebElement> elements = driver.findElements(locator);

        WebElement bestMatch = null;
        int shortestText = Integer.MAX_VALUE;

        // Find the smallest visible element containing the menu text
        for (WebElement element : elements) {

            try {
                String text = element.getText()
                    .toLowerCase(Locale.ROOT).trim();

                if (element.isDisplayed()
                    && text.contains(wanted)
                    && !text.isEmpty()
                    && text.length() < shortestText) {

                    bestMatch = element;
                    shortestText = text.length();
                }

            } catch (Exception ignored) {
            }
        }

        Assert.assertNotNull(
            bestMatch,
            menuName + " menu not found"
        );

        // Find clickable parent and click it
        WebElement clickable = (WebElement)
            ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].closest("
                + "'a,button,[role=button],[role=link],[onclick]')"
                + " || arguments[0];",
                bestMatch
            );

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});",
            clickable
        );

        try {
            clickable.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                clickable
            );
        }

        System.out.println(menuName + " menu clicked");
    }

    // FIND VISIBLE TEXT METHOD
    private WebElement findVisibleText(String text) {

        String lower = text.toLowerCase(Locale.ROOT).trim();

        By locator = By.xpath(
            "//*[contains(translate(normalize-space(.),"
            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
            + "'" + lower + "')]"
        );

        wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(locator)
        );

        List<WebElement> elements = driver.findElements(locator);

        WebElement bestMatch = null;
        int shortestText = Integer.MAX_VALUE;

        for (WebElement element : elements) {

            try {
                String actualText = element.getText()
                    .toLowerCase(Locale.ROOT).trim();

                if (element.isDisplayed()
                    && actualText.contains(lower)
                    && !actualText.isEmpty()
                    && actualText.length() < shortestText) {

                    bestMatch = element;
                    shortestText = actualText.length();
                }

            } catch (Exception ignored) {
            }
        }

        Assert.assertNotNull(
            bestMatch,
            text + " not displayed"
        );

        return bestMatch;
    }

    // TEST 1 - LOGIN
    @Test(priority = 1)
    public void loginTest() {

        ensureLoggedIn();

        String expectedResult = "/dashboard";
        String actualResult = driver.getCurrentUrl();

        System.out.println("Expected: " + expectedResult);
        System.out.println("Actual: " + actualResult);

        Assert.assertTrue(
            actualResult.contains(expectedResult),
            "Login Failed"
        );

        System.out.println("TEST 1 PASSED - Login");
    }

    // TEST 2 - DASHBOARD
    @Test(priority = 2)
    public void dashboardTest() {

        ensureLoggedIn();

        String expectedResult = "/dashboard";
        String actualResult = driver.getCurrentUrl();

        Assert.assertTrue(
            actualResult.contains(expectedResult),
            "Dashboard Not Opened"
        );

        System.out.println("TEST 2 PASSED - Dashboard");
    }

    // TEST 3 - CLOCK IN
    @Test(priority = 3)
    public void clockInTest() {

        ensureLoggedIn();

        By buttonsLocator = By.xpath(
            "//button | //a | //*[@role='button'] | //*[@role='link']"
        );

        List<WebElement> buttons = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                buttonsLocator
            )
        );

        boolean found = false;

        for (WebElement button : buttons) {

            try {
                if (!button.isDisplayed()) {
                    continue;
                }

                String text = button.getText()
                    .toLowerCase(Locale.ROOT).trim();

                if (text.contains("clock in")) {

                    button.click();
                    found = true;
                    System.out.println("Clock In Button Clicked");
                    break;
                }

                if (text.contains("clock out")) {

                    found = true;
                    System.out.println("Already Clocked In");
                    break;
                }

            } catch (Exception ignored) {
            }
        }

        Assert.assertTrue(
            found,
            "Clock In / Clock Out Button Not Found"
        );

        System.out.println("TEST 3 PASSED - Clock In");
    }

    // TEST 4 - ATTENDANCE PAGE
    @Test(priority = 4)
    public void attendancePageTest() {

        clickMenu("Attendance");

        WebElement heading = findVisibleText("Attendance Dashboard");

        Assert.assertTrue(
            heading.isDisplayed(),
            "Attendance Page Not Opened"
        );

        System.out.println("TEST 4 PASSED - Attendance Page");
    }

    // TEST 5 - MONTHLY SUMMARY
    @Test(priority = 5)
    public void monthlySummaryTest() {

        clickMenu("Attendance");

        WebElement summary = findVisibleText("Monthly Summary");

        Assert.assertTrue(
            summary.isDisplayed(),
            "Monthly Summary Not Displayed"
        );

        System.out.println("TEST 5 PASSED - Monthly Summary");
    }

    // TEST 6 - ATTENDANCE HISTORY
    @Test(priority = 6)
    public void attendanceHistoryTest() {

        clickMenu("Attendance");

        WebElement history = findVisibleText("Attendance History");

        Assert.assertTrue(
            history.isDisplayed(),
            "Attendance History Not Displayed"
        );

        System.out.println("TEST 6 PASSED - Attendance History");
    }

    // TEST 7 - LEAVE PAGE
    @Test(priority = 7)
    public void leavePageTest() {

        clickMenu("Leaves");

        WebElement heading = findVisibleText("Leaves Dashboard");

        Assert.assertTrue(
            heading.isDisplayed(),
            "Leave Page Not Opened"
        );

        System.out.println("TEST 7 PASSED - Leave Page");
    }

    // TEST 8 - LEAVE HISTORY
    @Test(priority = 8)
    public void leaveHistoryTest() {

        clickMenu("Leaves");

        WebElement history = findVisibleText("Leave History");

        Assert.assertTrue(
            history.isDisplayed(),
            "Leave History Not Displayed"
        );

        System.out.println("TEST 8 PASSED - Leave History");
    }

    // TEST 9 - LOGOUT
    @Test(priority = 9)
    public void logoutTest() {

        ensureLoggedIn();

        WebElement logoutButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(translate(normalize-space(.),"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
                + "'logout')]")
            )
        );

        logoutButton.click();

        wait.until(ExpectedConditions.urlContains("/login"));

        String expectedResult = "/login";
        String actualResult = driver.getCurrentUrl();

        System.out.println("Expected: " + expectedResult);
        System.out.println("Actual: " + actualResult);

        Assert.assertTrue(
            actualResult.contains(expectedResult),
            "Logout Failed"
        );

        System.out.println("TEST 9 PASSED - Logout");
    }

    // CLOSE BROWSER
    @AfterClass(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
            System.out.println("Browser Closed");
        }
    }
}