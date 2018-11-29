package util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Helpers {
    private static final int TIME_OUT_IN_SECOND = 10;

    public static void waitAndClick(WebDriver webDriver, String xpath) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIME_OUT_IN_SECOND);
            WebElement webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(xpath)));
            webElement.click();
        } catch (Exception e) {
        }

    }

    public static void waitAndSelect(WebDriver webDriver, String xpath, String select) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIME_OUT_IN_SECOND);
            WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(xpath)));
            new Select(webElement).selectByVisibleText(select);
        } catch (Exception e) {
        }

    }

    public static void waitAndSendKeys(WebDriver webDriver, String xpath, String sendKeys) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIME_OUT_IN_SECOND);
            WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(xpath)));
            webElement.sendKeys(sendKeys);
        } catch (Exception e) {
            int i;
        }
    }

    public static WebElement waitforPresence(WebDriver webDriver, String xpath) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIME_OUT_IN_SECOND);
            return wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(xpath)));
        } catch (Exception e) {
        }
        return null;
    }

    public static WebElement waitforClickable(WebDriver webDriver, String xpath) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIME_OUT_IN_SECOND);
            return wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(xpath)));
        } catch (Exception e) {
        }
        return null;
    }
}
