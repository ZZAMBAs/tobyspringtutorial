package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;

import java.util.List;

// 서비스 패키지는 주로 비즈니스 로직이 오는 패키지이다.
public class UserService{
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50; // 상수 중복 방지.
    public static final int MIN_RECCOUNT_FOR_GOLD = 30;
    private UserDao userDao;
    private UserServicePolicy userServicePolicy; // 업그레이드 정책 변경 가능성을 염두한 정책 분리.

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserServicePolicy(UserServicePolicy userServicePolicy){
        this.userServicePolicy = userServicePolicy;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        for (User user : users)
            if (userServicePolicy.canUpgradeLevel(user))
                userServicePolicy.upgradeLevel(user);
    } // 코드 성격 통합, 재사용성 코드 분리, 코드 가독성 향상 등의 이유로 리팩토링.

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
