package com.ione88.myParse.parse.yandex.zen;

import com.ione88.myParse.entity.News;

import java.util.ArrayList;

public interface ZenParser {

    ArrayList<News> parser(String city);
}

