package com.ione88.myParse;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ione88.myParse.parse.MyParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

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

        myParser.parseYandexNews(userCity).forEach(news ->
                sendPut(new StringEntity((new Gson()).toJson(news), ContentType.APPLICATION_JSON), "news"));
        log.debug("Новости отправлены на сервер!");

        myParser.parseYandexZen(userCity).forEach(news ->
                sendPut(new StringEntity((new Gson()).toJson(news), ContentType.APPLICATION_JSON), "news"));
        log.debug("Лента Дзен отправлена на сервер!");

        myParser.parseDnsBest(userCity).forEach(product -> {
            sendPut(new StringEntity((new Gson()).toJson(product), ContentType.APPLICATION_JSON), "product");
            log.debug("в " + product.getAvailables().size() + " магазинах DNS г. " + userCity + " есть информация о товаре: " + product.getName());
            product.getAvailables().forEach(available ->
                    sendPut(new StringEntity((new Gson()).toJson(available), ContentType.APPLICATION_JSON), "available"));
        });
        log.debug("Товары из магазина ДНС отправлены на сервер!");

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


    private static void sendPut(StringEntity params, String entityUrl) {

        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {

            HttpPut request = new HttpPut("http://localhost:8080/" + entityUrl + "/add" );
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            //handle response here...

        } catch (Exception ex) {
            System.out.println(ex);
            log.error("Ошибка отправки сообщения на сервер на Сервер! \n"+ex);
        }
    }


}