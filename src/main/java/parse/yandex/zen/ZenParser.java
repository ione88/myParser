package parse.yandex.zen;

import parse.yandex.News;

import java.util.ArrayList;

public interface ZenParser {

    ArrayList<News> parser(String city);
}

