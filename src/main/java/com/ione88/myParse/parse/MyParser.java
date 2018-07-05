package com.ione88.myParse.parse;

import com.google.inject.Inject;
import com.ione88.myParse.entity.Product;
import com.ione88.myParse.parse.dns.best.BestParser;
import com.ione88.myParse.entity.News;
import com.ione88.myParse.parse.yandex.news.NewsParser;
import com.ione88.myParse.parse.yandex.zen.ZenParser;

import java.util.ArrayList;

public class MyParser {
    private NewsParser yandexNews;
    private ZenParser yandexZen;
    private BestParser dnsBest;

    @Inject
    public MyParser(NewsParser newsParserNews, ZenParser zenParserZen, BestParser bestParserBest) {
        this.yandexNews = newsParserNews;
        this.yandexZen = zenParserZen;
        this.dnsBest = bestParserBest;
    }

    public ArrayList<News> parseYandexNews(String city) {
        return yandexNews.parser(city);
    }

    public ArrayList<News> parseYandexZen(String city) {
        return yandexZen.parser(city);
    }

    public ArrayList<Product> parseDnsBest(String city) {
        return dnsBest.parser(city);
    }
}
