package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.repository.Level;
import com.example.tobyspringtutorial.modules.repository.User;
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
        for (User user : users){
            Boolean change; // 변경 flag

            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50){ // BASIC 레벨 업그레이드
                user.setLevel(Level.SILVER);
                change = true;
            }
            else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30){ // SILVER 레벨 업그레이드
                user.setLevel(Level.GOLD);
                change = true;
            }
            else if (user.getLevel() == Level.GOLD)
                change = false;
            else
                change = false;

            if (change)
                userDao.update(user);
        }
    }

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
