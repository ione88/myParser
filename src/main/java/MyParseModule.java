import com.google.inject.AbstractModule;
import parse.dns.best.DnsBest;
import parse.dns.best.BestParser;
import parse.yandex.news.NewsParser;
import parse.yandex.news.YandexNews;
import parse.yandex.zen.YandexZen;
import parse.yandex.zen.ZenParser;

public class MyParseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ZenParser.class).to(YandexZen.class);
        bind(NewsParser.class).to(YandexNews.class);
        bind(BestParser.class).to(DnsBest.class);
    }
}