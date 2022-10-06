package com.example.tobyspringtutorial.forTest.service;

import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.service.UserServiceImpl;

import java.util.List;

public class TestUserService extends UserServiceImpl {
    @Override
    public List<User> getAll() { // readOnly 테스트 용으로 오버라이딩.
        for (User user : super.getAll())
            super.update(user); // 강제로 쓰기 명령. 이 때 읽기 전용 속성이 있다면 여기서 예외가 터져야 한다.
        return null;
    }
}
