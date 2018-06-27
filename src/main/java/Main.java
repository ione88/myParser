import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import parse.MyParser;
import parse.dns.Available;
import parse.dns.Product;
import parse.yandex.News;
import util.DataSourceModule;
import util.DataSourceMySQL;
import util.Insert;

import javax.sql.DataSource;
import java.util.Scanner;

public class Main {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        //пользователь вводит свой город
        String userCity = enterCity("Санкт-Петербург");
        //внедряем зависимости в классы парсеры
        Injector injector = Guice.createInjector(new MyParseModule());
        MyParser myParser = injector.getInstance(MyParser.class);
        //внедряем зависимость в класс по работе с БД
        Injector injectorSQL = Guice.createInjector(new DataSourceModule());
        DataSourceMySQL dataSource = injectorSQL.getInstance(DataSourceMySQL.class);
        //собираем Новости с главной страницы Яндекса в БД
        RunParseYandexNews(dataSource.getDataSource(), myParser, userCity);
        //собираем Дзен ленту с главной страницы Яндекса в БД
        RunParseYandexZen(dataSource.getDataSource(), myParser, userCity);
        //собираем лучшие товары (и их доступность) с главной страницы DNS в БД
        RunParseDnsBest(dataSource.getDataSource(), myParser, userCity);
    }

    private static String enterCity(String defualtCity) {
        Scanner in = new Scanner(System.in);
        System.out.print("Введите ваш город или нажмите Enter для г." + defualtCity + ": ");
        String userCity = in.nextLine();
        if (userCity.isEmpty()) {
            log.info("Пользователь не указал город, используем город по умолчанию: " + defualtCity);
            return defualtCity;
        }
        log.debug("Пользователь указал город: " + userCity);
        return userCity;
    }

    private static void RunParseYandexNews(DataSource dataSource, MyParser parser, String City) {
        for (News news : parser.parseYandexNews(City))
            Insert.news(dataSource, news);
        log.debug("Новости спарсились и уже в базе данных!");
    }

    private static void RunParseYandexZen(DataSource dataSource, MyParser parser, String City) {
        for (News news : parser.parseYandexZen(City))
            Insert.news(dataSource, news);
        log.debug("Лента Дзен спарсилась и уже в базе данных!");
    }

    private static void RunParseDnsBest(DataSource dataSource, MyParser parser, String City) {
        for (Product product : parser.parseDnsBest(City)) {
            Insert.product(dataSource, product);
            //добавляем информацию о доступности товара в магазинах города
            log.debug("в "+product.getAvailables().size()+" магазинах DNS г. "+ City+" есть информация о товаре: " + product.getName());
            for (Available available : product.getAvailables())
                Insert.available(dataSource, available);
        }
        log.debug("Товары из магазина ДНС спарсилась и уже в базе данных!");
    }
}