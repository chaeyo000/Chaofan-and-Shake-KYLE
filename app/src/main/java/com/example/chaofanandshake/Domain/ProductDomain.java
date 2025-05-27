package com.example.chaofanandshake.Domain;

import java.io.Serializable;

public class ProductDomain implements Serializable {
    private String imageName;
    private String title;
    private String description;
    private double price;
    private int quantity = 1;  // default 1
    private double totalPrice;  // dagdag para sa total price ng product sa cart

    // Default no-args constructor
    public ProductDomain() {
    }

    // Parameterized constructor without quantity (default quantity = 1)
    public ProductDomain(String imageName, String title, String description, double price) {
        this.imageName = imageName;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = 1;
        this.totalPrice = price * quantity;
    }

    public ProductDomain(String imageName, String title, String description, double price, int someInt) {
        this.imageName = imageName;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = someInt;
        this.totalPrice = price * quantity;
    }
    // Getters

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Setter for quantity - automatically update totalPrice when quantity changes
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price * this.quantity;
    }

    // Optional: Override toString() for easy debugging
    @Override
    public String toString() {
        return "ProductDomain{" +
                "imageName='" + imageName + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
