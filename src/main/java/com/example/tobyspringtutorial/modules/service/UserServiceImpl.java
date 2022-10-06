package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;

import java.util.List;

// 서비스 패키지는 주로 비즈니스 로직이 오는 패키지이다.
public class UserServiceImpl implements UserService{
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50; // 상수 중복 방지.
    public static final int MIN_RECCOUNT_FOR_GOLD = 30;
    protected UserDao userDao;
    protected UserServicePolicy userServicePolicy; // 업그레이드 정책 변경 가능성을 염두한 정책 분리.

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserServicePolicy(UserServicePolicy userServicePolicy){
        this.userServicePolicy = userServicePolicy;
    }

    @Override
    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users)
            if (userServicePolicy.canUpgradeLevel(user))
                userServicePolicy.upgradeLevel(user);

    }

    @Override
    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    // 특별히 부가할 기능이 없으니 위임한다.
    @Override
    public User get(String id) {
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }
}
