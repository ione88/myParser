import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ione88.myParse.MyParseModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import com.ione88.myParse.parse.MyParser;

import java.util.Scanner;

public class Main {
    // Инициализация логера
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {


        //пользователь вводит свой город
        String userCity = enterCity("Тверь");
        //внедряем зависимости в классы парсеры
        Injector injector = Guice.createInjector(new MyParseModule());
        MyParser myParser = injector.getInstance(MyParser.class);
        //внедряем зависимость в класс по работе с БД
        //Injector injectorSQL = Guice.createInjector(new DataSourceModule());
        //DataSourceMySQL dataSource = injectorSQL.getInstance(DataSourceMySQL.class);

        myParser.parseYandexNews(userCity).forEach(news ->
                sendPost(new StringEntity((new Gson()).toJson(news), ContentType.APPLICATION_JSON), "news"));
        log.debug("Новости отправлены на сервер!");

        myParser.parseYandexZen(userCity).forEach(news ->
                sendPost(new StringEntity((new Gson()).toJson(news), ContentType.APPLICATION_JSON), "news"));
        log.debug("Лента Дзен отправлена на сервер!");

        myParser.parseDnsBest(userCity).forEach(product -> {
            sendPost(new StringEntity((new Gson()).toJson(product), ContentType.APPLICATION_JSON), "product");
            log.debug("в " + product.getAvailables().size() + " магазинах DNS г. " + userCity + " есть информация о товаре: " + product.getName());

            product.getAvailables().forEach(available ->
                    sendPost(new StringEntity((new Gson()).toJson(available), ContentType.APPLICATION_JSON), "available"));
        });
        log.debug("Товары из магазина ДНС спарсилась и уже в базе данных!");
    }

    private static String enterCity(String defualtCity) {
        Scanner in = new Scanner(System.in);
        System.out.print("Введите ваш город или сразу нажмите Enter для г." + defualtCity + ": ");
        String userCity = in.nextLine();
        if (userCity.isEmpty()) {
            log.info("Пользователь не указал город, используем город по умолчанию: " + defualtCity);
            return defualtCity;
        }
        log.debug("Пользователь указал город: " + userCity);
        return userCity;
    }


    private static void sendPost(StringEntity params, String configUrl) {

        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {

            HttpPost request = new HttpPost("http://localhost:8080/" + configUrl);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            //handle response here...

        } catch (Exception ex) {
            log.error("Ошибка отправки новостей на Сервер!");
        }
    }


}