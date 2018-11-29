package util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import parse.avito.Post;
import parse.dns.Available;
import parse.dns.Product;
import parse.dns.best.DnsBest;
import parse.yandex.News;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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

    public static void post(Post post) {
        try {
            File description = createFile(post);
            saveDescription(post, description);
            saveImages(post, description);
        } catch (Exception e) {
            log.error("Ошибка сохранения объявления: \n" + post.getUrl() + "\n "+  post.getName());
            log.error(e);
        }
    }

    private static File createFile(Post post) throws IOException {
        String[] urlSplit = post.getUrl().split("/");
        File path = new File("debug\\" + urlSplit[urlSplit.length - 1]);
        if (!path.exists())
            path.mkdirs();
        // TODO: save in file with name info.txt
        File description = new File(path, post.getName().replace("/", "___") + ".txt");
            if (!description.exists())
                description.createNewFile();
        return description;
    }

    private static void saveDescription(Post post, File description) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(description.getAbsoluteFile());
        try {
            out.print(post.getDescription());
        } catch (Exception e) {
            log.error("Ошибка сохранения описания объявления: \n" + post.getUrl() + "\n "+  post.getName());
            log.error(e);
        }
        finally {
            out.close();
        }
    }

    private static void saveImages(Post post, File description) throws FileNotFoundException {
        // скачать картинки
        for (String url : post.getImages()) {
            try {
                String[] urlSplit = url.split("/");
                URL imageURL = new URL("https:" + url);
                File image = new File(description.getParent(), urlSplit[urlSplit.length - 1]);
                if (!image.exists())
                    image.createNewFile();
                BufferedImage saveImage = ImageIO.read(imageURL);
                ImageIO.write(saveImage, "jpg", image);
                // получить байты по ссылке
                // конвертировать webp to jpg
                //сохранить файл
            } catch (Exception e) {
                log.error("Ошибка сохранения изображений объявления: \n" + post.getUrl() + "\n "+  post.getName());
                log.error(e);
            }
        }
    }
}