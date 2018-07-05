package com.ione88.myParse.entity;

public class Available {
    //Код товара – уникальное поле, целое
    private Integer code;
    //город магазина
    private String city;
    //название магазина
    private String shop;
    //количество товара в наличии
    private Integer count;
    //количество дней ожидания при заказе
    private Integer waitingDays;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getWaitingDays() {
        return waitingDays;
    }

    public void setWaitingDays(Integer waitingDays) {
        this.waitingDays = waitingDays;
    }
}
