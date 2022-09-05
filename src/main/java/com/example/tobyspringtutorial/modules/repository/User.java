package com.example.tobyspringtutorial.modules.repository;

public class User {
    private String id;
    private String userName;
    private String password;
    private Level level;
    private int login;
    private int recommend;

    public User(String id, String userName, String password, Level level, int login, int recommend) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.level = level;
        this.login = login;
        this.recommend = recommend;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
