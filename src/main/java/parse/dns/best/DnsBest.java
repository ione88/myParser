package parse.dns.best;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
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
    private String city;

    // Инициализация логера
    private static final Logger log = Logger.getLogger(DnsBest.class);


    @Override
    public ArrayList<Product> parser(String userCity) {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1920, 1024));
        driver.get("https://www.dns-shop.ru/");
        //создаем новый пустой список продуктов, в который будем парсить
        ArrayList<Product> products = new ArrayList<>();
        // передаю в функцию изменения города, город который хочет пользователь и город указаный сейчас на сайте
        try {
            city = changeCity(userCity, driver.findElement(By.className("w-choose-city-widget")).getText());
            //получаем список url всех продуктов из категории лучшие предложения
            List<WebElement> bestOffers = driver.findElement(By.className("shopwindow-products")).findElements(By.xpath("//a[@data-product-param='name']"));
            //сохраняем все ссылки в список, что бы потом идти по нему
            ArrayList<String> urls = new ArrayList<>();
            bestOffers.forEach(offer -> urls.add(offer.getAttribute("href")));
            //идём по список страниц с товарами и сохраняем в нашем списке products
            urls.forEach(url -> products.add(getProduct(url)));
        } catch (WebDriverException wde) {
            log.error("Ошибка при парсинге товаров DNS \n" + wde);
        }

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
        // поле для ввода Название города
        WebElement cityInput;
        // кликаем на кнопку выбор города
        driver.findElement(By.className("w-choose-city-widget")).click();
        //выбрали строку для ввода
        String xpathToCityInput = "//div[contains(@class,'select-city-modal') and not(contains(@id,'select-city'))]//input[@placeholder='Название города']";
        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToCityInput)));
        // выбираем форму для ввода города
        cityInput = driver.findElement(By.xpath(xpathToCityInput));
        //отправляем название города
        cityInput.sendKeys(userCity);
        try {
            // если город найден однозначно (появилась подсказка), то меняет город
            if (driver.findElement(By.xpath("//div[contains(@class,'show-hint')]")).isDisplayed()) {
                currentCity = driver.findElement(By.xpath("//div[contains(@class,'show-hint')]/b")).getText().replaceAll("\\s+", "");
                cityInput.sendKeys(Keys.ENTER);
            }
            // иначе сработает исключение
        } catch (WebDriverException wde) {
            log.info("Пользователь ввёл несуществующий город, остаёмся в г. " + currentCity);
            driver.findElement(By.xpath("//div[contains(@class,'select-city-modal') and not(contains(@id,'select-city'))]//button[contains(@data-dismiss,'modal')]")).click();
        }
        return currentCity;
    }

    private Product getProduct(String url) {
        driver.get(url);
        Product product = new Product();
        try {
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
        } catch (WebDriverException wde) {
            log.error("Ошибка при парсинге товара г. " + city + " на странице товара " + url);
            log.error(wde);
        }

        //Сохраняем информацию о доступности в магазинах
        product.setAvailables(getAllAvailables(product.getCode()));
        return product;
    }

    //считывает Характеристики в объект
    private ArrayList<ParametrsMap> getAllParametrsMaps() {
        //создаем новый пустой список параметров
        ArrayList<ParametrsMap> allParametrsMaps = new ArrayList<>();
        //открываем вкладку характеристики
        driver.findElement(By.linkText("Характеристики")).click();
        //получаем список параметров (характеристик)
        List<WebElement> parametrs = driver.findElement(By.id("characteristics")).findElements(By.tagName("tr"));
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
        //создаем новый пустой список наличии в магазинах
        ArrayList<Available> allAvailables = new ArrayList<>();

        try {
            (new WebDriverWait(driver, 30))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='clearfix']//a[contains(@role,'button')]")));
            driver.findElement(By.xpath("//div[@class='clearfix']//a[contains(@role,'button')]")).click();
            //получаем список магазинов (характеристик) avails-item row avails-items
            String xpathToShops = "//div[contains(@class,'avails-item') and contains(@class,'row') and not(contains(@class,'hidden'))]";
            (new WebDriverWait(driver, 30))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToShops)));
            List<WebElement> shops = driver.findElements(By.xpath(xpathToShops));
            //для каждого магазина сохраняем доступность
            shops.forEach(shop -> {
                Available available = new Available();
                available.setCode(productCode);
                available.setCity(city);
                available.setShopName(shop.findElement(By.xpath(".//div[@class='shop-name']//a")).getText());
                // проверяем если товар в наличии то записываем его количество
                try {
                    shop.findElement(By.xpath(".//div[contains(@class,'available')]"));
                    String order = shop.findElement(By.xpath(".//div[contains(@class,'col-3')]")).getText();
                    available.setCount(getCount(order));
                    available.setWaitingForOrderInDays(0);
                    // иначе записываем требуемое количество дней ожидания товара
                } catch (WebDriverException wde) {
                    available.setCount(0);
                    String order = shop.findElement(By.xpath(".//div[contains(@class,'col-3')]")).getText();
                    available.setWaitingForOrderInDays(getCount(order));
                }
                allAvailables.add(available);
            });

        } catch (WebDriverException wde) {
            //открываем вкладку наличии в магазинах button
            log.info("Возможно нет товара в магазинах г. " + city + " код товара: " + productCode + " или изменилась страница товара");
        }
        //возвращаем объект с параметрами
        return allAvailables;
    }

    private Integer getCount(String order) {
        //"послезавтра" послезавтра будет через 2 дня
        if (order.lastIndexOf("послезавтра") != -1)
            return 2;
        //"завтра" завтра будет через 1 день
        if (order.lastIndexOf("завтра") != -1)
            return 1;
        //3,4..21 дней для доставки
        try {
            Integer count = Integer.parseInt(order.replaceAll("[^0-9+]", ""));
            if (count > 9) log.error("Возможно ошибка, целых:" + count +"шт.\n Исходная строка: "+order);
            return count;
        } catch (Exception e) {
            log.error("Неизвестный формат количества товара/дней ожиданий:" + order);
            return 0;
        }
    }
}