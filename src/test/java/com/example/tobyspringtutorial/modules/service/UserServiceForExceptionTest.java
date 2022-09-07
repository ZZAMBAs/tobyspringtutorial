package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.repository.UserDao;

// 레벨 업그레이드 도중 서버 문제로 예외 상황이 터졌을 때를 위한 테스트용 대역 클래스
public class UserServiceForExceptionTest extends UserService{
    private final String id;
    public UserServiceForExceptionTest(String id) { // 예외를 발생시킬 User의 id 지정.
        this.id = id;
    }

    @Override
    public void setUserDao(UserDao userDao) {
        super.setUserDao(userDao);
        UserServicePolicyForExceptionTest userServicePolicyTest = new UserServicePolicyForExceptionTest(id);
        userServicePolicyTest.setUserDao(userDao);
        userServicePolicy = userServicePolicyTest;
    }
}
