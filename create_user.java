package pageobjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class create_user {
    WebDriver driver;

    // Constructor
    public create_user(WebDriver driver) {
        this.driver = driver;
    }

    // Locators
    By usernameField = By.name("txtLogin");
    By passwordField = By.name("txtPassword");
    By loginButton = By.id("btnSignIn");

    // Methods to interact with elements
    public void enterUsername(String username) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(7));
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        driver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }
}