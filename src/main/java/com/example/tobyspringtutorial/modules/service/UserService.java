package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;

import java.util.List;

// 서비스 패키지는 주로 비즈니스 로직이 오는 패키지이다.
public class UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        for (User user : users)
            if (canUpgradeLevel(user))
                upgradeLevel(user);
    } // 코드 성격 통합, 재사용성 코드 분리, 코드 가독성 향상 등의 이유로 리팩토링.

    public boolean canUpgradeLevel(User user){
        Level userLevel = user.getLevel();
        switch (userLevel){
            case BASIC: return (user.getLogin() >= 50);
            case SILVER: return (user.getRecommend() >= 30);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + userLevel); // 언체크 예외
                // 이 예외는 새 레벨이 추가 되었을 때, 개발자가 이 코드를 수정하지 않았을 경우 발생하여 코드를 수정하도록 돕는다.
        }
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user); // DB에도 적용하는 것을 잊지 말자.
    }

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
