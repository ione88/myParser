package parse.avito.post;

import parse.avito.Post;
import parse.yandex.News;

import java.net.MalformedURLException;
import java.util.ArrayList;

public interface PostParser {

    ArrayList<Post> parser();
    String poster(Post post);
    void initAndLogin();
    void quit();
}

