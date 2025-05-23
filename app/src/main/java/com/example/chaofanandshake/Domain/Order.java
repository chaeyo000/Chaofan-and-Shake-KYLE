package com.example.chaofanandshake.Domain;

public class Order {
    private int id;
    private String orderSummary;
    private String phone;
    private String paymentMethod;
    private double total;

    public Order(int id, String orderSummary, String phone, String paymentMethod, double total) {
        this.id = id;
        this.orderSummary = orderSummary;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.total = total;
    }

    // getters here...
    public int getId() { return id; }
    public String getOrderSummary() { return orderSummary; }
    public String getPhone() { return phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotal() { return total; }
}
