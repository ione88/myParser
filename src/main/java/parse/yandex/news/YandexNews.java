package parse.yandex.news;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import parse.yandex.News;
import java.util.ArrayList;
import java.util.List;

public class YandexNews implements NewsParser {
    private WebDriver driver;

    @Override
    public ArrayList<News> parser(String userCity){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://yandex.ru/");
        //создаем новый пустой список новостей, которые будем парсить
        ArrayList<News> newsfeed = new ArrayList<News>();

        //получаем список 4  новостей из главное категории
        List<WebElement> mainnewsfeed = driver.findElements(By.xpath("//div[contains(@class,'content-tabs__items_active_true')]//ol[not(contains(@class,'news__animation-list'))]//a"));
        //добавляем главные новости в список
        mainnewsfeed.forEach(mainnews -> newsfeed.add(getNews(mainnews,"main")));

        //переходим на вкладку региональные новости
        // tabnews_region
        driver.findElement(By.xpath("//div[@id='tabnews_region']//a")).click();
        //получаем список 4  новостей из главное категории
        List<WebElement> regionnewsfeed = driver.findElements(By.xpath("//div[contains(@class,'content-tabs__items_active_true')]//ol[not(contains(@class,'news__animation-list'))]//a"));
        //добавляем региональные новости в список
        regionnewsfeed.forEach(regionnews -> newsfeed.add(getNews(regionnews,"region")));
        //закрываем браузер
        driver.quit();
        //возвращаем найденые новости
        return newsfeed;
    }

    private News getNews(WebElement webnews, String typeOfNews){
        News news = new News();
        news.setUrl(webnews.getAttribute("href"));
        news.setTitle(webnews.getAttribute("aria-label"));
        news.setTypeOfNews(typeOfNews);
        return news;
    }
}
