package fr.eseo.tauri.selenium;

import org.junit.jupiter.api.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumTauriTest {
        private static final String URL = System.getProperty("seleniumServerUrl", "http://localhost:5173/");
        private static final String settings = System.getProperty("seleniumOptions", "off");
        private static final String LOGIN = "p.l@tauri.com";
        private static final String PASSWORD = "pl";
        private static final String TITLE = "Bienvenue sur Tauri !";
        private static WebDriver webdriver;


        @BeforeAll
        public static void beforeTest(){
                WebDriverManager.safaridriver().setup();
                ChromeOptions options = new ChromeOptions();
                if(settings.equals("on")){
                        options.addArguments("--no-sandbox");
                        options.addArguments("--headless");
                        options.addArguments("--ignore-certificate-errors");
                }
                SeleniumTauriTest.webdriver = new ChromeDriver(options);
        }

        @Test
        @Order(1)
        void login(){
                SeleniumTauriTest.webdriver.get(SeleniumTauriTest.URL+"login");
                WebDriverWait wait = new WebDriverWait(SeleniumTauriTest.webdriver, Duration.ofSeconds(10));
                WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("text-dark-blue")));

                Assertions.assertEquals(SeleniumTauriTest.TITLE,
                        titleElement.getText(), "Title");
        }


        @AfterAll
        public static void afterTests(){
                SeleniumTauriTest.webdriver.close();
        }
}
