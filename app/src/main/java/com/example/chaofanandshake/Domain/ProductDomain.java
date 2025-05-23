package com.example.chaofanandshake.Domain;

import java.io.Serializable;

public class ProductDomain implements Serializable {
    private String imageName;
    private String title;
    private double price;
    private int quantity = 1;  // default 1

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


    public ProductDomain(String imageName, String title, double price) {
        this.imageName = imageName;
        this.title = title;
        this.price = price;
    }

    public String getImageName() {
        return imageName;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }
}

