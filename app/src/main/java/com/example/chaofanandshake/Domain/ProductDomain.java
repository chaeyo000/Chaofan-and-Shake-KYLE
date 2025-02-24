package com.example.chaofanandshake.Domain;

public class ProductDomain {
    private String imageName; // Kept consistent with ProductAdapter.java
    private String title;
    private double price;

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
