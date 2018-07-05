package util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import com.ione88.myParse.entity.Available;
import com.ione88.myParse.entity.Product;
import com.ione88.myParse.entity.News;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Insert {

    // Инициализация логера
    private static final Logger log = Logger.getLogger(Insert.class);

    public static void news(DataSource dataSource, News news) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO yandex_news (`updated`, title, url, type) VALUES (CURRENT_TIMESTAMP(), ?, ?, ?)" +
                                    "ON DUPLICATE KEY UPDATE url=?",
                            news.getTitle(), news.getUrl(), news.getType(),
                            news.getUrl());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления новости в базу даных");
            log.error(sqle);
        }
    }

    public static void product(DataSource dataSource, Product product) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO dns_products (code, name, price, description, parametrs_json, url) VALUES (?, ?, ?, ?, ?, ?)" +
                                    "ON DUPLICATE KEY UPDATE name = ?, price = ?, description = ?, parametrs_json = ?, url = ?",
                            product.getCode(), product.getName(), product.getPrice(), product.getDescription(), product.getParametrsJson(), product.getUrl(),
                            product.getName(), product.getPrice(), product.getDescription(), product.getParametrsJson(), product.getUrl());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления товара DNS в базу даных");
            log.error(sqle);
        }
    }

    public static void available(DataSource dataSource, Available available) {
        try {
            new QueryRunner(dataSource).update
                    ("INSERT INTO dns_products_available (code, city, shop, count, waitingForOrderInDays, `updated`) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP())" +
                                    "ON DUPLICATE KEY UPDATE count=?, waitingForOrderInDays=?, updated=CURRENT_TIMESTAMP()",
                            available.getCode(), available.getCity(), available.getShop(), available.getCount(), available.getWaitingDays(),
                            available.getCount(), available.getWaitingDays());
        } catch (SQLException sqle) {
            log.error("Ошибка добавления информации о наличии товара DNS в базу даных");
            log.error(sqle);
        }
    }
}