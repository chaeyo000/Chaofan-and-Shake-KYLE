package com.example.chaofanandshake.Domain;

public class Order {
    private int id;
    private String orderSummary;
    private String phone;
    private String paymentMethod;

    private String name;
    private double total;

    private String username;



    public Order(int id, String name, String orderSummary, String phone, String username, String paymentMethod, double total) {
        this.id = id;
        this.name = name;
        this.orderSummary = orderSummary;
        this.phone = phone;
        this.username = username;
        this.paymentMethod = paymentMethod;
        this.total = total;
    }

    // getters here...


    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() { return id; }
    public String getOrderSummary() { return orderSummary; }
    public String getPhone() { return phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotal() { return total; }
}
