package parse.yandex.zen;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import parse.yandex.News;

import java.util.ArrayList;
import java.util.List;

public class YandexZen implements ZenParser {
    private WebDriver driver;
    private JavascriptExecutor jse;

    @Override
    public ArrayList<News> parser(String userCity) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://yandex.ru/");
        jse = (JavascriptExecutor)driver;
        jse.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.linkText("Дзен")));
        //опустились вниз и ожидаем подгрузки ленты Дзен
        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("feed__row")));
        //получаем список Дзен новостей
        List<WebElement> feeds = driver.findElements(By.className("doc__link"));
        //создаем новый пустой список новостей, которые будем парсить
        ArrayList<News> newsfeed = new ArrayList<>();
        //добавиляем новости в наш список
        feeds.forEach(webnews -> newsfeed.add(getNews(webnews,"Zen")));
        //закрываем браузер
        driver.quit();
        //возвращаем найденые новости
        return newsfeed;
    }

    private News getNews(WebElement webnews, String typeOfNews){
        News news = new News();
        news.setUrl(webnews.getAttribute("href"));
        news.setTitle(webnews.findElement(By.className("clamp__visible-tokens")).getText());
        news.setTypeOfNews(typeOfNews);
        return news;
    }
}
