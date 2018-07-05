package com.ione88.myParse.parse.dns;

import java.util.HashMap;

public class ParametrsMap {
    private String parametrType;
    private HashMap<String,String> parametrs;


    public ParametrsMap(String parametrType){
        this.parametrType = parametrType;
        this.parametrs = new HashMap<>();
    }

    public void parametrsput(String key, String value){
        this.parametrs.put(key,value);
    }
}
