package com.hai.model;

import com.alibaba.fastjson.JSONObject;

public class Book {
    private int id;
    private String name;
    private double price;
    private String description;

    public Book() {
    }

    public Book(int id, String name, double price, String desc) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
