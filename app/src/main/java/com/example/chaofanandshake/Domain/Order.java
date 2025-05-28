package com.example.chaofanandshake.Domain;

public class Order {
    private int id;
    private String orderSummary;
    private String phone;
    private String paymentMethod;
    private String name;
    private double total;
    private String username;
    private String date; // ✅ Added date

    public Order(int id, String name, String orderSummary, String phone, String username, String paymentMethod, double total, String date) {
        this.id = id;
        this.name = name;
        this.orderSummary = orderSummary;
        this.phone = phone;
        this.username = username;
        this.paymentMethod = paymentMethod;
        this.total = total;
        this.date = date; // ✅ Assign date
    }

    public int getId() {
        return id;
    }

    public String getOrderSummary() {
        return orderSummary;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalPrice() {
        return total;
    }

    public String getCustomerName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() { // ✅ Added getter
        return date;
    }

    // Optional setters if needed
}
