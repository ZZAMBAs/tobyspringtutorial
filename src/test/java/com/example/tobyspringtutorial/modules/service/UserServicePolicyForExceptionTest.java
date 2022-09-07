package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.exception.TestUserServiceException;
import com.example.tobyspringtutorial.modules.objects.User;

public class UserServicePolicyForExceptionTest extends UserServicePolicyDefault{
    private final String id;

    public UserServicePolicyForExceptionTest(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }
}
