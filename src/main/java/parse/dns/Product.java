package parse.dns;

import java.util.ArrayList;

public class Product {
    //Наименование - строка
    private String name;
    //Код товара – уникальное поле, целое
    private Integer code;
    //Цена - целое
    private Integer price;
    //Описание – строка
    private String description;
    //Url - строка
    private String url;

    //список магазинов, где товар в наличии или доступен заказ
    private ArrayList<Available> availables;

    // GSON строка с параметрами.
    private String parametrs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParametrs() {
        return parametrs;
    }

    public void setParametrs(String parametrs) {
        this.parametrs = parametrs;
    }

    public ArrayList<Available> getAvailables() {
        return availables;
    }

    public void setAvailables(ArrayList<Available> avalibles) {
        this.availables = avalibles;
    }
}
