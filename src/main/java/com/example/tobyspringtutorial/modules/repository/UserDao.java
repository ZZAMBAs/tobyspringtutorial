package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.modules.objects.User;

import java.util.List;

public interface UserDao { // 인터페이스로 만들어 기술에 독립적으로 만든다.
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    void update(User user);
}
