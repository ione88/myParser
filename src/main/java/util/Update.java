package util;

import org.apache.log4j.Logger;
import parse.avito.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Update {

    // Инициализация логера
    private static final Logger log = Logger.getLogger(Insert.class);

    public static void post(Post post, String newUrl){
        if (newUrl.equals(""))
            return;
        try {
            String[] urlSplit = newUrl.split("/");
            File status = new File(post.getPath(), urlSplit[urlSplit.length - 1] + ".upload");
            if (!status.exists())
                status.createNewFile();
        } catch (Exception e) {
            log.error("Ошибка загрузки объявления: \n" + post.getUrl() + "\n " + post.getName());
            log.error(e);
        }
    }
}
