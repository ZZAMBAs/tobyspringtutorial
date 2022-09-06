package com.example.tobyspringtutorial.modules.objects;

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

    public void upgradeLevel(){ // User가 직접 자신의 정보를 바꾸는 비즈니스 로직을 갖는다.
        // 객체는 객체 자신이 능동적으로 상태를 바꿔야 한다. 따라서 서비스 계층보단 자기 자신이 자기 자신의 상태를 바꾸는게 바람직하다.
        Level nextLevel = this.level.nextLevel();
        if (nextLevel == null)
            throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
        // 이미 UserService의 canUpgradeLevel() 메서드가 이 역할을 수행하지만, UserService가 아닌 곳에서도 User 오브젝트를
        // 사용할 수 있기 때문에 자체적으로도 가지고 있는 것이 안전하다.
        else
            this.level = nextLevel;
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
