package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.User;

public interface UserServicePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
