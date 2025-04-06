package stepdefinitions;

import io.cucumber.java.en.*;
import org.junit.Assert;
import io.cucumber.java.After;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;
import java.util.List;
import pageobjects.create_user;

public class onesourcestepdefinition {

    WebDriver driver;
    create_user Login;

    @Given("the user navigates to onesource login page")
    public void the_user_navigates_to_onesource_login_page() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://uat.onesourcetax.com/");
        Login = new create_user(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleContains("ONESOURCE Login"));
    }

    @When("user types username & password and click login button")
    public void enter_credentials() {
        Login.enterUsername("klein_uat_qv6");
        Login.enterPassword("A34694220a4328__");
        Login.clickLoginButton();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleContains("ONESOURCE Home"));
    }

    @Then("Onesource homepage is displayed")
    public void homepage_is_displayed() {
        Assert.assertTrue(driver.getTitle().contains("ONESOURCE Home"));
    }

    @When("the user navigates to the user creation page")
    public void navigates_to_the_user_creation_page() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement adminSetup = wait
                .until(ExpectedConditions.elementToBeClickable(By.id("HomeProduct-AdministrationSetup")));
        adminSetup.click();
        Thread.sleep(7000);

        List<WebElement> moreButtonList = driver.findElements(By.xpath("//button[contains(., 'More')]"));
        if (!moreButtonList.isEmpty()) {
            WebElement moreButton = moreButtonList.get(0);
            if (moreButton.isDisplayed()) {
                moreButton.click();
                Thread.sleep(3000);
            }
        }

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("setup-iframe-app-container"));

        try {
            WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[@type='button' and .//span[text()='Add']]")));
            js.executeScript("arguments[0].scrollIntoView(true);", addButton);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", addButton);
            Thread.sleep(5000);
            System.out.println("Add button clicked successfully.");
        } catch (TimeoutException e) {
            System.err.println("Add button was not found inside the iframe.");
            throw e;
        }

        driver.switchTo().defaultContent();
    }

    @And("enters {string}, {string}, {string}, {string}, and selects {string}")
    public void enters_user_details(String id, String name, String email, String password, String userType)
            throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean switchedToIframe = false;

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            driver.switchTo().frame(iframe);
            List<WebElement> idFieldList = driver.findElements(
                    By.xpath("//input[@id='universalId' or @name='universalId' or @formcontrolname='universalId']"));
            if (!idFieldList.isEmpty()) {
                switchedToIframe = true;
                break;
            }
            driver.switchTo().defaultContent();
        }

        if (!switchedToIframe) {
            throw new NoSuchElementException("Field 'universalId' not found in any iframe.");
        }

        WebElement idField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@id='universalId' or @name='universalId' or @formcontrolname='universalId']")));
        wait.until(ExpectedConditions.visibilityOf(idField));
        wait.until(ExpectedConditions.elementToBeClickable(idField)).sendKeys(id);

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));
        nameField.sendKeys(name);

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailField.sendKeys(email);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys(password);

        WebElement confirmPasswordField = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));
        confirmPasswordField.sendKeys(password);

        try {
            WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("userChangePasswordOnSignin")));
            js.executeScript(
                    "arguments[0].checked = false;" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    checkbox);
            System.out.println("Checkbox was successfully unchecked using JavaScript.");
        } catch (Exception e) {
            System.out.println("Failed to uncheck the checkbox via JavaScript: " + e.getMessage());
        }

        Thread.sleep(1000);

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("ut")));
        Select select = new Select(dropdown);
        select.selectByVisibleText(userType);
        System.out.println("User created with: " + id + ", " + name + ", " + email + ", " + userType);

        Thread.sleep(3000);

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'bento-wizard-next-button')]//span[text()='NEXT']/..")));
        nextButton.click();
        System.out.println("NEXT button clicked.");

        Thread.sleep(5000);

        if (switchedToIframe) {
            driver.switchTo().defaultContent();
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
