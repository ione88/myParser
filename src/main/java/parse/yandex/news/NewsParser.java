package parse.yandex.news;

import parse.yandex.News;

import java.util.ArrayList;

public interface NewsParser {

    ArrayList<News> parser(String city);
}

