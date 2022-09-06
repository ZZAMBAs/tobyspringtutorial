package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;

import static com.example.tobyspringtutorial.modules.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.tobyspringtutorial.modules.service.UserService.MIN_RECCOUNT_FOR_GOLD;

public class UserServicePolicyDefault implements UserServicePolicy{ // 평소 업그레이드 정책.
    private UserDao userDao;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }
    public boolean canUpgradeLevel(User user){
        Level userLevel = user.getLevel();
        switch (userLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOUNT_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + userLevel); // 언체크 예외
                // 이 예외는 새 레벨이 추가 되었을 때, 개발자가 이 코드를 수정하지 않았을 경우 발생하여 코드를 수정하도록 돕는다.
        }
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user); // DB에도 적용하는 것을 잊지 말자.
    }
}
