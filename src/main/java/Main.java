import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import parse.MyParser;
import parse.avito.Post;
import parse.dns.Available;
import parse.dns.Product;
import parse.yandex.News;
import util.*;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        //пользователь вводит свой город
        String userCity = enterCity("Пермь");
        //внедряем зависимости в классы парсеры
        Injector injector = Guice.createInjector(new MyParseModule());
        MyParser myParser = injector.getInstance(MyParser.class);
        //внедряем зависимость в класс по работе с БД
        Injector injectorSQL = Guice.createInjector(new DataSourceModule());
        DataSourceMySQL dataSource = injectorSQL.getInstance(DataSourceMySQL.class);
        //собираем Объявления с Авито
        RunParseAvito(myParser);
        //RunPosterAvito(myParser);
        /*
        //собираем Новости с главной страницы Яндекса в БД
        RunParseYandexNews(dataSource.getDataSource(), myParser, userCity);
        //собираем Дзен ленту с главной страницы Яндекса в БД
        RunParseYandexZen(dataSource.getDataSource(), myParser, userCity);
        //собираем лучшие товары (и их доступность) с главной страницы DNS в БД
        RunParseDnsBest(dataSource.getDataSource(), myParser, userCity);
        */
    }

    private static String enterCity(String defaultSite) {
        /*Scanner in = new Scanner(System.in);
        System.out.print("Введите ваш город или нажмите Enter для г." + defualtCity + ": ");
        String userCity = in.nextLine();
        if (userCity.isEmpty()) {
            log.info("Пользователь не указал город, используем город по умолчанию: " + defualtCity);
            return defualtCity;
        }
        log.info("Пользователь указал город: " + userCity);
        return userCity;
        */
        return defaultSite;
    }

    private static void RunParseAvito(MyParser parser) {
        for (Post post : parser.parseAvitoPost())
            Insert.post(post);
        log.info("Объявления спарсились и уже сохранены в папку!");
    }

    private static void RunPosterAvito(MyParser parser) {
        parser.loginAvito();
        for (Post post : Select.post(152)) {
            Update.post(post, parser.postingAvitoPost(post));
        }
        parser.quitAvito();
        log.info("Объявления уже на сайте!");
    }

    private static void RunParseYandexNews(DataSource dataSource, MyParser parser, String City) {
        for (News news : parser.parseYandexNews(City))
            Insert.news(dataSource, news);
        log.info("Новости спарсились и уже в базе данных!");
    }

    private static void RunParseYandexZen(DataSource dataSource, MyParser parser, String City) {
        for (News news : parser.parseYandexZen(City))
            Insert.news(dataSource, news);
        log.info("Лента Дзен спарсилась и уже в базе данных!");
    }

    private static void RunParseDnsBest(DataSource dataSource, MyParser parser, String City) {
        for (Product product : parser.parseDnsBest(City)) {
            Insert.product(dataSource, product);
            //добавляем информацию о доступности товара в магазинах города
            log.info("в "+product.getAvailables().size()+" магазинах DNS г. "+ City+" есть информация о товаре: " + product.getName());
            for (Available available : product.getAvailables())
                Insert.available(dataSource, available);
        }
        log.info("Товары из магазина ДНС спарсилась и уже в базе данных!");
    }
}