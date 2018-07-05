package com.ione88.myParse.parse.yandex.news;

import com.ione88.myParse.entity.News;

import java.util.ArrayList;

public interface NewsParser {

    ArrayList<News> parser(String city);
}

