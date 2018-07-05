package com.ione88.myParse.parse.yandex.zen;

import com.ione88.myParse.entity.News;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class YandexZen implements ZenParser {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(YandexZen.class);

    private WebDriver driver;
    private JavascriptExecutor jse;

    @Override
    public ArrayList<News> parser(String userCity) {
        try {
            driver = new ChromeDriver();
            driver.manage().window().setSize(new Dimension(1920, 1024));
            driver.get("https://yandex.ru/");
        } catch (Exception e) {
            log.error("Ошибка при запуске браузера на парсинге Дзен ленты Yandex.ru \n" + e);
        }

        //создаем новый пустой список новостей, которые будем парсить
        ArrayList<News> newsfeed = new ArrayList<>();

        try {
            jse = (JavascriptExecutor) driver;
            //опустились вниз и ожидаем подгрузки ленты Дзен
            jse.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.linkText("Дзен")));
            (new WebDriverWait(driver, 20))
                    .until(ExpectedConditions.presenceOfElementLocated(By.className("feed__row")));
            //получаем список Дзен новостей
            List<WebElement> feeds = driver.findElements(By.className("doc__link"));
            //добавиляем новости в наш список
            feeds.forEach(webnews -> newsfeed.add(getNews(webnews, "Zen")));
        } catch (WebDriverException wde) {
            log.error("Ошибка чтения Дзен ленты с главной страницы Яндекса\n" + wde);
        }
        //закрываем браузер
        driver.quit();
        //возвращаем найденые новости
        return newsfeed;
    }

    private News getNews(WebElement webnews, String type) {
        News news = new News();
        news.setUrl(webnews.getAttribute("href"));
        news.setTitle(webnews.findElement(By.xpath(".//span[@class='clamp__visible-tokens']")).getText());
        news.setType(type);
        return news;
    }
}