package com.example.tobyspringtutorial.forTest.service;

import com.example.tobyspringtutorial.exceptions.TestUserServiceException;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.service.UserServicePolicyDefault;

public class TestUserServicePolicy extends UserServicePolicyDefault {
    private final String id;

    public TestUserServicePolicy(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }
}