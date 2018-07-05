package com.ione88.myParse.entity;

import java.util.ArrayList;

public class Product {
    //Код товара – уникальное поле, целое
    private Integer code;
    //Наименование - строка
    private String name;
    //Цена - целое
    private Integer price;
    //Описание – строка
    private String description;
    //Url - строка
    private String url;
    // GSON строка с параметрами.
    private String parametrsJson;
    //список магазинов, где товар в наличии или доступен заказ
    private ArrayList<Available> availables;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getParametrsJson() {
        return parametrsJson;
    }

    public void setParametrsJson(String parametrsJson) {
        this.parametrsJson = parametrsJson;
    }

    public ArrayList<Available> getAvailables() {
        return availables;
    }

    public void setAvailables(ArrayList<Available> availables) {
        this.availables = availables;
    }
}