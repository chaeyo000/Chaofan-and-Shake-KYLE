package com.example.chaofanandshake.Domain;

public class Order {
    private int id;
    private String orderSummary;
    private String phone;
    private String paymentMethod;
    private String name;
    private double total;
    private String username;
    private String date;
    private String status;
    private long orderPlacedTimestamp;

    public Order(int id, String name, String orderSummary, String phone, String username,
                 String paymentMethod, double total, String date, String status, long orderPlacedTimestamp) {
        this.id = id;
        this.name = name;
        this.orderSummary = orderSummary;
        this.phone = phone;
        this.username = username;
        this.paymentMethod = paymentMethod;
        this.total = total;
        this.date = date;
        this.status = status;
        this.orderPlacedTimestamp = orderPlacedTimestamp;
    }

    // Getters
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

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    // Setters (optional, but required for updating fields like status)
    public void setStatus(String status) {
        this.status = status;
    }
    public void setOrderSummary(String orderSummary) {
        this.orderSummary = orderSummary;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setCustomerName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getOrderPlacedTimestamp() {
        return orderPlacedTimestamp;  // <-- New getter
    }





    public void setDate(String date) {
        this.date = date;
    }
}
