package com.example.chaofanandshake.Domain;

public class User {
    private int id;
    private String username;
    private String phone;
    private String name;

    public User(int id, String username, String phone, String name) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }
}
