package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.User;

public interface UserService { // AOP
    void add(User user);
    void upgradeLevels();
}
