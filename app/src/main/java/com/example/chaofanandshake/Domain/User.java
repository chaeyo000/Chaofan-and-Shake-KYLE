package com.example.chaofanandshake.Domain;

public class User {
    private int id;
    private String name;
    private String username;
    private String phone;

    public User(int id, String name, String username, String phone) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.phone = phone;
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
