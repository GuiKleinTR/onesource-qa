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
        driver.get("https://qa.onesourcetax.com/");
        Login = new create_user(driver);
    }

    @When("user types username & password and click login button")
    public void enter_credentials() {
        Login.enterUsername("klein_qa_v6f");
        Login.enterPassword("Test@1234");
        Login.clickLoginButton();

        // Correção do WebDriverWait
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

        // Clica no menu de Administração e Configuração
        WebElement adminSetup = wait
                .until(ExpectedConditions.elementToBeClickable(By.id("HomeProduct-AdministrationSetup")));
        adminSetup.click();

        // Aguarda 4 segundos antes de continuar
        Thread.sleep(7000);

        // Verifica se o botão "More" precisa ser clicado antes de exibir o botão "Add"
        List<WebElement> moreButtonList = driver.findElements(By.xpath("//button[contains(., 'More')]"));
        if (!moreButtonList.isEmpty()) {
            WebElement moreButton = moreButtonList.get(0);
            if (moreButton.isDisplayed()) {
                moreButton.click();
                Thread.sleep(3000); // Tempo extra para o menu expandir
            }
        }

        // Verifica se o botão está dentro de um iframe
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        if (!iframes.isEmpty()) {
            driver.switchTo().frame(0); // Alterna para o primeiro iframe
        }

        // Aguarda o botão "Add" estar presente usando o XPath fornecido
        List<WebElement> addButtonList = driver.findElements(By.xpath("//*[@id='toolbar-id-0']/li[1]/button"));

        if (addButtonList.isEmpty()) {
            throw new NoSuchElementException("O botão 'Add' não foi encontrado na página.");
        }

        WebElement addButton = addButtonList.get(0);

        // Rola a tela até o botão estar visível
        js.executeScript("arguments[0].scrollIntoView(true);", addButton);

        // Aguarda até que o botão esteja clicável e clica nele
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();

        // Sai do iframe caso tenha entrado
        if (!iframes.isEmpty()) {
            driver.switchTo().defaultContent();
        }
    }

    @And("enters {string}, {string}, {string}, {string}, and selects {string}")
    public void enters_user_details(String id, String name, String email, String password, String userType) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        boolean switchedToIframe = false;
     
        // 🔍 Tenta encontrar o iframe que contém o campo "universalId"
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            driver.switchTo().frame(iframe);
            List<WebElement> idFieldList = driver.findElements(By.xpath("//input[@id='universalId' or @name='universalId' or @formcontrolname='universalId']"));
     
            if (!idFieldList.isEmpty()) {
                System.out.println("Campo 'universalId' encontrado dentro de um iframe.");
                switchedToIframe = true;
                break;  // Parar a busca assim que encontrar
            }
            driver.switchTo().defaultContent();
        }
     
        if (!switchedToIframe) {
            throw new NoSuchElementException("O campo 'universalId' não foi encontrado em nenhum iframe.");
        }
     
        // 🔄 Espera até que o campo esteja presente e visível
        WebElement idField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@id='universalId' or @name='universalId' or @formcontrolname='universalId']")));
        wait.until(ExpectedConditions.visibilityOf(idField));
        wait.until(ExpectedConditions.elementToBeClickable(idField));
     
        // 🔄 Preenchendo os campos
        idField.sendKeys(id);
     
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));
        nameField.sendKeys(name);
     
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailField.sendKeys(email);
     
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys(password);
     
        WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));
        confirmPasswordField.sendKeys(password);
     
        // 🔄 Selecionando o User Type no dropdown
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("ut")));
        Select select = new Select(dropdown);
        select.selectByVisibleText(userType);
     
        System.out.println("Usuário preenchido: " + id + ", " + name + ", " + email + ", " + userType);
     
        // 🚀 Sai do iframe se necessário
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