package util;

import org.apache.commons.dbutils.QueryRunner;
import parse.dns.Available;
import parse.dns.Product;
import parse.yandex.News;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Insert {

    public static void news(DataSource dataSource, News news) throws SQLException {
        new QueryRunner(dataSource).update
                ("INSERT INTO yandexnews (updateDate, title, url, type) VALUES (CURRENT_TIMESTAMP(), ?, ?, ?)" +
                                "ON DUPLICATE KEY UPDATE url=?",
                        news.getTitle(), news.getUrl(), news.getTypeOfNews(),
                        news.getUrl());
    }

    public static void product(DataSource dataSource, Product product) throws SQLException {
        new QueryRunner(dataSource).update
                ("INSERT INTO dnsproducts (code, name, price, description, parametrs, url) VALUES (?, ?, ?, ?, ?, ?)" +
                                "ON DUPLICATE KEY UPDATE name = ?, price = ?, description = ?, parametrs = ?, url = ?",
                        product.getCode(), product.getName(), product.getPrice(), product.getDescription(), product.getParametrs(), product.getUrl(),
                        product.getName(), product.getPrice(), product.getDescription(), product.getParametrs(), product.getUrl());
    }

    public static void available(DataSource dataSource, Available available) throws SQLException {
        new QueryRunner(dataSource).update
                ("INSERT INTO available (code, city, shop, count, waitingForOrderInDays, updateDate) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP())" +
                                "ON DUPLICATE KEY UPDATE count=?, waitingForOrderInDays=?, updateDate=CURRENT_TIMESTAMP()",
                        available.getCode(), available.getCity(), available.getShopName(), available.getCount(), available.getWaitingForOrderInDays(),
                        available.getCount(), available.getWaitingForOrderInDays());
    }
}
