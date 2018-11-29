package parse;

import com.google.inject.Inject;
import parse.avito.Post;
import parse.avito.post.PostParser;
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
    private PostParser avitoPost;


    @Inject
    public MyParser(NewsParser newsParserNews, ZenParser zenParserZen, BestParser bestParserBest, PostParser avitoParserPost) {
        this.yandexNews = newsParserNews;
        this.yandexZen = zenParserZen;
        this.dnsBest = bestParserBest;
        this.avitoPost = avitoParserPost;
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

    public ArrayList<Post> parseAvitoPost() {
        return avitoPost.parser();
    }

    public String postingAvitoPost(Post post) {
        return avitoPost.poster(post);
    }

    public void loginAvito() {
        avitoPost.initAndLogin();
    }

    public void quitAvito() {
        avitoPost.initAndLogin();
    }
}
