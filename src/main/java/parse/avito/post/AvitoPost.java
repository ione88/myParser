package parse.avito.post;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import parse.avito.Post;
import util.Helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class AvitoPost implements PostParser {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(AvitoPost.class);

    private WebDriver driver;


    @Override
    public ArrayList<Post> parser(){
        driver = new ChromeDriver();

        //создаем новый пустой список новостей, которые будем парсить
        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 1; i<5; i++) {
            driver.get("https://www.avito.ru/appshop/rossiya/telefony/iphone?p=" + i);
            List<WebElement> postDescriptions = driver.findElements(By.xpath("//div[contains(@class, 'item_table-description')]"));
            for(WebElement postDescription : postDescriptions) {
                Post telephone = getPost(postDescription);
                if (telephone.getUrl().split("/").length == 6)
                    posts.add(telephone);
            }
        }

        for (Post post : posts) {
            driver.get(post.getUrl());
            WebElement description = driver.findElement(
                    By.xpath("//div[contains(@class, 'item-description-html')" +
                            "or contains(@class, 'item-description-text')]"));
            post.setDescription(getDescription(description));

            List<WebElement> webImages = driver.findElements(
                    By.xpath("//div[contains(@class, 'gallery-extended-img-frame') " +
                            "and not(contains(@class, 'gallery-extended-img-frame_with-video')) " +
                            "and not(contains(@class, 'gallery-extended-img-frame-video-btn js-gallery-extended-img-frame-video-btn'))]"));
            post.setImages(getImages(webImages));
        }
        driver.quit();
        //возвращаем найденые новости
        return posts;
    }

    private List<String> getImages(List<WebElement> webImages) {
        List<String> images = new ArrayList<String>();
        for (WebElement webImage : webImages){
            images.add(webImage.getAttribute("data-url"));
        }
        return images;
    }

    private String getDescription(WebElement webDescription) {
        return webDescription.getText()
                .replace("ПРОДАЖА 100% ОРИГИНАЛЬНЫХ IPHONE ПО САМЫМ НИЗКИМ ЦЕНАМ.", "ПРОДАЖА 100% ОРИГИНАЛЬНЫХ IPHONE ПО САМЫМ НИЗКИМ ЦЕНАМ.\nГАРАНТИЯ 1 ГОД")
                .replace("Гаджет-Дисконт","SotikTeam")
                .replace("ул. Куйбышева, 16/1","ул.Большая Садовая, д. 46")
                .replace("г. Пермь, ул. Куйбышева, д. 16/1","г. Ростов-на-Дону, пересечение ул. Б. Садовая и пр. Будённовский")
                .replace("Куйбышева","Большая Садовая")
                .replace("Пермь","Ростов-на-Дону")
                .replace("16/1","46")
                .replace("ежедневно с 10:00 до 21:00", "ежедневно с 10:00 до 20:00");
    }

    private Post getPost(WebElement webPost){
        Post post = new Post();
        post.setName(webPost.findElement(By.xpath("./div/h3/a")).getText());
        post.setUrl(webPost.findElement(By.xpath("./div/h3/a")).getAttribute("href"));
        return post;
    }

    public String poster(Post post) {
        try {
            addPost(post);
            for (String image : post.getImages()) {
                addImage(image);
            }
            continueFree();
            return hidePost(post);
        } catch (Exception e) {
            quit();
            log.error("Error: can't add this" + e.getMessage());
            return "";
        }
    }

    public void initAndLogin() {
        try {
            initPlugin();
            login();
        } catch (Exception e) {
            log.error("Error: can't add this" + e.getMessage());
            quit();
        }
    }

    private void continueFree() throws InterruptedException {
        sleep(10000);
        Helpers.waitAndClick(driver, "//label[contains(@class, 'packages-tab_free')]");
        Helpers.waitAndClick(driver, "//button[contains(@class, 'button-origin_large') and contains(text(), 'Обычная продажа')]");
        Helpers.waitAndClick(driver, "//a[contains(@class, 'mini-antifraud-submit-button') and contains(text(), 'Продолжить')]");
        sleep(10000);
        Helpers.waitforPresence(driver, "//div[contains(@class, 'vas-services__list')]/label");

        for (WebElement service : driver.findElements(By.xpath("//div[contains(@class, 'vas-services__list')]/label"))) {
            service.click();
        }
        Helpers.waitAndClick(driver, "//button[contains(@class, 'button-origin_large')]");
    }

    private void addImage(String image) {
        WebElement sendImage = Helpers.waitforPresence(driver, "//input[contains(@class, 'uploader-input-file')]");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sendImage);
        sendImage.sendKeys(image);
    }

    public void quit() {
        if (driver == null)
            return;
        driver.get("https://www.avito.ru/profile/exit");
        driver.quit();
    }

    private String hidePost(Post post) {
        driver.get(driver.findElement(By.xpath("//a[contains(text(), '" + post.getName() + "')]")).getAttribute("href"));
        Helpers.waitAndClick(driver, "//button[contains(@class, 'button-root_size-s') " +
                " and contains(text(), 'Снять c публикации')]");
        Helpers.waitAndClick(driver, "//button[contains(@class, 'button-root_size-m') " +
                " and contains(text(), 'Другая причина')]");
        return driver.getCurrentUrl();
    }

    private void addPost(Post post) {
        driver.get("https://www.avito.ru/additem");
        String[] category = post.getUrl().split("/");
        for (int i = 0; i < 3; i++) {
            Helpers.waitAndClick(driver, "//button[contains(text(), '" + category[i] + "')]");
        }
        if (category.length > 3) {
            Helpers.waitAndSelect(driver, "//select[contains(@id, 'params[480]')]", category[3]);
        }
        Helpers.waitAndSelect(driver, "//select[contains(@id, 'params[2822]')]", "Товар приобретён на продажу");

        Helpers.waitAndSendKeys(driver, "//input[contains(@name, 'title')]", post.getName());
        Helpers.waitAndSendKeys(driver, "//div[contains(@role, 'textbox')]", post.getDescription());

    }

    private void initPlugin() {
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File("anticaptcha.crx"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
        driver.get("chrome-extension://lncaoejhfdpcafpkkcddpjnhnodcajfg/options.html");
        Helpers.waitAndSendKeys(driver, "//input[contains(@name, 'account_key') and not(contains(@name, 'account_key_title')) ]", "598bf7ec44ab255faeac6fb62fdda69b");
        Helpers.waitAndClick(driver, "//input[contains(@class, 'save_button')]");
    }

    private void login() throws InterruptedException, NoSuchElementException {
        driver.get("https://www.avito.ru/#login");
        Helpers.waitAndSendKeys(driver, "//input[contains(@name, 'login')]", "time-moscow@mail.ru");
        Helpers.waitAndSendKeys(driver, "//input[contains(@name, 'password')]", "1989sotik");
        driver.findElement(By.xpath("//button[contains(@name, 'submit')]")).submit();
        for (int i = 0; i < 100; i++) {
            sleep(3000);
            if (Helpers.waitforPresence(driver, "//a/span[contains(text(),'SotikTeam')]") != null)
                break;
        }
        driver.findElement(By.xpath("//a/span[contains(text(),'SotikTeam')]"));
    }
}
