package util;

import org.apache.log4j.Logger;
import parse.avito.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Select {

    // Инициализация логера
    private static final Logger log = Logger.getLogger(Insert.class);

    public static ArrayList<Post> post(final int LIMIT){
        ArrayList<Post> posts = new ArrayList<>();
        File path = new File("debug\\");
        for (File dir : path.listFiles()) {
                if (dir.isFile())
                    continue;
                boolean isUpload = false;
                Post post = new Post();
                post.setPath(dir.getPath());
                for (File file : dir.listFiles()) {
                    if (file.getName().contains(".txt")) {
                        // TODO: read data from file inside
                        post.setName( file.getName().split("\\.")[0].replace("___","/") );
                        Scanner in = null;
                        try {
                            StringBuilder desc = new StringBuilder();
                            in = new Scanner(file);
                            while(in.hasNext())
                                desc.append(in.nextLine() + "\n");
                            post.setDescription(desc.toString());
                        } catch (FileNotFoundException e) {
                            log.error("Ошибка чтения  описания объявления: \n" + file.getName() + "\n "+  file.getParentFile().getName());
                            log.error(e);
                        } finally {
                            in.close();
                        }
                        if (file.getName().contains("Watch")) {
                            post.setUrl("Бытовая электроника/Телефоны/Аксессуары/Гарнитуры и наушники");
                        } else {
                            post.setUrl("Бытовая электроника/Телефоны/iPhone");
                        }
                        continue;
                    }
                    if (isUpload = file.getName().contains(".upload")) {
                        break;
                    }
                    if (isUpload = file.getName().contains(".blocked")) {
                        break;
                    }
                    ArrayList<String> images = new ArrayList<>();
                    images.add(file.getAbsolutePath());
                    if (images.size() == 0 ) {
                        log.error("Нет изображений: \n" + file.getName() + "\n "+  file.getParentFile().getName());
                    }
                    post.setImages(images);
                }
                if (isUpload)
                    continue;
                posts.add(post);
                if (posts.size() >= LIMIT)
                    break;
        }
        return posts;
    }
}
