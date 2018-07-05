package com.ione88.myParse.parse.yandex.news;

import com.ione88.myParse.entity.News;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YandexNews implements NewsParser {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(YandexNews.class);

    private WebDriver driver;

    @Override
    public ArrayList<News> parser(String userCity) {
        try {
            driver = new ChromeDriver();

            driver.manage().window().setSize(new Dimension(1920, 1024));

            driver.get("https://yandex.ru/");
        } catch (Exception e) {
            log.error("Ошибка при запуске браузера на парсинге новостной ленты Yandex.ru \n" + e);
        }
        //создаем новый пустой список новостей, которые будем парсить
        ArrayList<News> newsfeed = new ArrayList<>();

        try {
            //получаем список 4  новостей из главное категории
            List<WebElement> mainnewsfeed = driver.findElements(By.xpath("//div[contains(@class,'content-tabs__items_active_true')]//ol[not(contains(@class,'news__animation-list'))]//a"));
            //добавляем главные новости в список
            mainnewsfeed.forEach(mainnews -> newsfeed.add(getNews(mainnews, "main")));

            //переходим на вкладку региональные новости
            // tabnews_region
            driver.findElement(By.xpath("//div[@id='tabnews_region']//a")).click();
            //получаем список 4  новостей из главное категории
            List<WebElement> regionnewsfeed = driver.findElements(By.xpath("//div[contains(@class,'content-tabs__items_active_true')]//ol[not(contains(@class,'news__animation-list'))]//a"));
            //добавляем региональные новости в список
            regionnewsfeed.forEach(regionnews -> newsfeed.add(getNews(regionnews, "region")));
        } catch (WebDriverException wde) {
            log.error("Ошибка чтения новостей с главной страницы Яндекса\n" + wde);
        }

        //закрываем браузер
        driver.quit();
        //возвращаем найденые новости
        return newsfeed;
    }

    private News getNews(WebElement webnews, String type) {
        News news = new News();
        news.setUrl(webnews.getAttribute("href"));
        news.setTitle(webnews.getAttribute("aria-label"));
        news.setType(type);
        return news;
    }
}