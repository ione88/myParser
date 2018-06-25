package parse;

import com.google.inject.Inject;
import parse.dns.Product;
import parse.dns.best.BestParser;
import parse.yandex.News;
import parse.yandex.news.NewsParser;
import parse.yandex.zen.ZenParser;

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
