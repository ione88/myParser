package com.ione88.myParse;

import com.google.inject.AbstractModule;
import com.ione88.myParse.parse.dns.best.BestParser;
import com.ione88.myParse.parse.dns.best.DnsBest;
import com.ione88.myParse.parse.yandex.news.NewsParser;
import com.ione88.myParse.parse.yandex.news.YandexNews;
import com.ione88.myParse.parse.yandex.zen.YandexZen;
import com.ione88.myParse.parse.yandex.zen.ZenParser;

public class MyParseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ZenParser.class).to(YandexZen.class);
        bind(NewsParser.class).to(YandexNews.class);
        bind(BestParser.class).to(DnsBest.class);
    }
}

