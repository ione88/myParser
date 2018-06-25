import com.google.inject.Guice;
import com.google.inject.Injector;
import parse.MyParser;
import parse.dns.Available;
import parse.dns.Product;
import parse.yandex.News;
import util.DataSourceModule;
import util.DataSourceMySQL;
import util.Insert;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Scanner;

import static java.lang.Long.max;
import static java.lang.Long.parseLong;


public class Main {

    public static void main(String[] args) throws Exception {
        //пользователь вводит свой город
        String userCity = enterCity("Ростов-на-Дону");

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
        if (userCity.isEmpty()) return defualtCity;
        return userCity;
    }

    private static void RunParseYandexNews(DataSource dataSource, MyParser parser, String City) throws SQLException {
        for (News news : parser.parseYandexNews(City))
            Insert.news(dataSource, news);
    }

    private static void RunParseYandexZen(DataSource dataSource, MyParser parser, String City) throws SQLException {
        for (News news : parser.parseYandexZen(City))
            Insert.news(dataSource, news);
    }

    private static void RunParseDnsBest(DataSource dataSource, MyParser parser, String City) throws SQLException {
        for (Product product : parser.parseDnsBest(City)) {
            Insert.product(dataSource, product);
            //добавляем информацию о доступности товара в магазинах города
            for (Available available : product.getAvailables())
                Insert.available(dataSource, available);
        }
    }

}
