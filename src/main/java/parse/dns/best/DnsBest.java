package parse.dns.best;

import com.google.gson.Gson;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parse.dns.Available;
import parse.dns.ParametrsMap;
import parse.dns.Product;

import java.util.ArrayList;
import java.util.List;

public class DnsBest implements BestParser {

    private WebDriver driver;
    private String City;

    @Override
    public ArrayList<Product> parser(String userCity) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.dns-shop.ru/");

        //создаем новый пустой список продуктов, которые будем парсить
        ArrayList<Product> products = new ArrayList<>();

        // передаю в функцию изменения города, город который хочет пользователь и город указаный сейчас на сайте
        City = changeCity(userCity, driver.findElement(By.className("w-choose-city-widget")).getText());

        //получаем список url всех продуктов из категории лучшие предложения
        List<WebElement> bestOffers = driver.findElement(By.className("shopwindow-products")).findElements(By.xpath("//a[@data-product-param='name']"));

        //сохраняем все ссылки в список, что бы потом идти по нему
        ArrayList<String> urls = new ArrayList<>();
        bestOffers.forEach(offer -> urls.add(offer.getAttribute("href")));

        //идём по список страниц с товарами и сохраняем в нашем списке products
        urls.forEach(url -> products.add(getproduct(url)));

        //закрываем браузер
        driver.quit();
        //возвращаем найденые товары
        return products;

    }

    private String changeCity(String userCity, String currentCity) {
        // если города равны, то ничего делать не нужно выходим из программы
        if (userCity.toLowerCase().equals(currentCity.toLowerCase())) {
            return currentCity;
        }
        // кликаем на кнопку
        driver.findElement(By.className("w-choose-city-widget")).click();

        //выбрали строку для ввода
        String xpathToCityInput = "//div[contains(@class,'select-city-modal') and not(contains(@id,'select-city'))]//input[@placeholder='Название города']";

        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToCityInput)));

        // выбираем форму для ввода города
        WebElement cityInput = driver.findElement(By.xpath(xpathToCityInput));
        //отправляем название города
        cityInput.sendKeys(userCity);
        try{
            // если город найден однозначно (появилась подсказка), то меняет город
            if (driver.findElement(By.xpath("//div[contains(@class,'show-hint')]")).isDisplayed()){
                currentCity = driver.findElement(By.xpath("//div[contains(@class,'show-hint')]/b")).getText().replaceAll("\\s+", "");
                cityInput.sendKeys(Keys.ENTER);
            }
            // иначе сработает исключение
        } catch (Exception exp) {
            driver.findElement(By.xpath("//div[contains(@class,'select-city-modal') and not(contains(@id,'select-city'))]//button[contains(@data-dismiss,'modal')]")).click();
        }
        return currentCity;
    }

    private Product getproduct(String url) {
        driver.get(url);
        Product product = new Product();

        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("price-item-description")));

        //Ссылка на страницу с товаром
        product.setUrl(url);
        //Наименование - строка
        product.setName(driver.findElement(By.className("price-item-title")).getText());
        //Код товара – уникальное поле, целое
        product.setCode(Integer.parseInt(driver.findElement(By.className("price-item-code")).findElement(By.tagName("span")).getText()));
        //Цена - целое
        product.setPrice(Integer.parseInt(driver.findElement(By.className("price_g")).findElement(By.tagName("span")).getAttribute("data-price-value")));
        //Описание – строка
        product.setDescription(driver.findElement(By.id("description")).findElement(By.tagName("p")).getText());
        //Превращаем объект с параметами в gson строку продукты
        product.setParametrs((new Gson()).toJson(getAllParametrsMaps()));
        //Сохраняем информацию о доступности в магазинах
        product.setAvailables(getAllAvailables(product.getCode()));


        return product;
    }


    //считывает Характеристики в объект
    private ArrayList<ParametrsMap> getAllParametrsMaps() {
        //открываем вкладку характеристики
        driver.findElement(By.linkText("Характеристики")).click();
        //получаем список параметров (характеристик)
        List<WebElement> parametrs = driver.findElement(By.id("characteristics")).findElements(By.tagName("tr"));
        ArrayList<ParametrsMap> allParametrsMaps = new ArrayList<>();
        //для каждый параметр сохраняем в объект
        parametrs.forEach(parametr -> {
            List<WebElement> row = parametr.findElements(By.tagName("td"));
            // если в строке только 1 столбец, значит это новый параметр, сохраняем его
            if (row.size() == 1) {
                // создать новый массив обекта
                allParametrsMaps.add(new ParametrsMap(row.get(0).getText()));
                //если 2 столбца, то значит в этой строке есть характеристика и значение параметра, сохраняем их.
            } else {
                // добавить в текущий массив новый элемент
                String key = row.get(0).findElement(By.tagName("span")).getText();
                String value = row.get(1).getText();
                Integer lastParametr = allParametrsMaps.size() - 1;

                allParametrsMaps.get(lastParametr).parametrsput(key, value);
            }
        });
        //возвращаем объект с параметрами
        return allParametrsMaps;
    }

    private ArrayList<Available> getAllAvailables(Integer productCode) {
        //открываем вкладку наличии в магазинах button
        driver.findElement(By.xpath("//div[@class='clearfix']//a[contains(@role,'button')]")).click();

        //получаем список магазинов (характеристик) avails-item row avails-items
        String xpathToShops = "//div[contains(@class,'avails-item') and contains(@class,'row')]";

        //ожидаем загрузки
        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToShops)));

        List<WebElement> shops = driver.findElements(By.xpath(xpathToShops));
        //создаем новый пустой список наличии в магазинах
        ArrayList<Available> allAvailables = new ArrayList<>();

        //для каждого магазина сохраняем доступность //
        shops.forEach(shop -> {
            Available available = new Available();
            available.setCode(productCode);
            available.setCity(City);
            available.setShopName(shop.findElement(By.xpath(".//div[@class='shop-name']//a")).getText());
            //узнаем число, это либо количество товара либо количество дней ожидания

            // проверяем если товар в наличии то записываем его количество
            if (shop.findElements(By.xpath(".//div[contains(@class,'available')]")).size() > 0) {
                available.setCount(getCountInShop(shop.findElement(By.xpath(".//div[contains(@class,'col-3')]"))));
                available.setWaitingForOrderInDays(0);
                // иначе то записываем требуемое количество дней ожидания товара
            } else {
                available.setCount(0);
                available.setWaitingForOrderInDays(WaitingForOrderInDays(shop.findElement(By.xpath(".//div[contains(@class,'col-3')]"))));
            }
            allAvailables.add(available);

        });
        //возвращаем объект с параметрами
        return allAvailables;
    }

    private Integer getCountInShop(WebElement order) {
        return Integer.parseInt(order.getText().replaceAll("[^0-9+]", ""));
    }

    private Integer WaitingForOrderInDays(WebElement order) {
        //"послезавтра" товар послезавтра будет 2 дней ждать
        if (order.getText().lastIndexOf("послезавтра") != -1)
            return 2;
        //"завтра" товар завтра будет 1 дней ждать
        if (order.getText().lastIndexOf("завтра") != -1)
            return 1;
        //"сегодня" товар сегодня будет 0 дней ждать
        if (order.getText().lastIndexOf("сегодня") != -1)
            return 0;
        //3,4..21 дней для доставки
        if (!order.getText().replaceAll("[^0-9+]", "").isEmpty()) {
            Integer wait = Integer.parseInt(order.getText().replaceAll("[^0-9+]", ""));
            //если получилось число больше 22, это уже не количество дней.
            if (wait < 22)
                return wait;
        }
        //тут не должны оказаться
        return 0;
    }
}