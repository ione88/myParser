package util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import parse.dns.Available;
import parse.dns.Product;
import parse.dns.best.DnsBest;
import parse.yandex.News;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Insert {

    // Инициализация логера
    private static final Logger log = Logger.getLogger(Insert.class);

    public static void news(DataSource dataSource, News news) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO yandexnews (update, title, url, type) VALUES (CURRENT_TIMESTAMP(), ?, ?, ?)" +
                                    "ON DUPLICATE KEY UPDATE url=?",
                            news.getTitle(), news.getUrl(), news.getTypeOfNews(),
                            news.getUrl());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления новости в базу даных");
            log.error(sqle);
        }
    }

    public static void product(DataSource dataSource, Product product) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO dnsproducts (code, name, price, description, parametrs, url) VALUES (?, ?, ?, ?, ?, ?)" +
                                    "ON DUPLICATE KEY UPDATE name = ?, price = ?, description = ?, parametrs = ?, url = ?",
                            product.getCode(), product.getName(), product.getPrice(), product.getDescription(), product.getParametrs(), product.getUrl(),
                            product.getName(), product.getPrice(), product.getDescription(), product.getParametrs(), product.getUrl());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления товара DNS в базу даных");
            log.error(sqle);
        }
    }

    public static void available(DataSource dataSource, Available available) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO available (code, city, shop, count, waitingForOrderInDays, update) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP())" +
                                    "ON DUPLICATE KEY UPDATE count=?, waitingForOrderInDays=?, updateDate=CURRENT_TIMESTAMP()",
                            available.getCode(), available.getCity(), available.getShopName(), available.getCount(), available.getWaitingForOrderInDays(),
                            available.getCount(), available.getWaitingForOrderInDays());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления товара DNS в базу даных");
            log.error(sqle);
        }
    }
}