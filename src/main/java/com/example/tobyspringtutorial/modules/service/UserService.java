package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional // @Transactional은 트랜잭션
public interface UserService {
    void add(User user);
    // 아래 get, getAll, deleteAll, update는 새로 추가하였다.
    // 다른 모듈에서 DAO를 거치려면 서비스 계층을 거치게 하는 것이 프록시 패턴에서의 트랜잭션 측면에서 좋기 때문에 추가한 것이다.
    // DAO 직접 접근은 밀접한 연관이 있는 서비스 계층에서만 하도록 한다.
    @Transactional(readOnly = true)
    User get(String id);
    @Transactional(readOnly = true)
    List<User> getAll();
    void deleteAll();
    void update(User user);
    void upgradeLevels();
}
