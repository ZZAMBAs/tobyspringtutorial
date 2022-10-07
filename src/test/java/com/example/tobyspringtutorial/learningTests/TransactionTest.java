package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DaoFactory.class)
//@Transactional(transactionManager = "transactionManager", readOnly = true) // 클래스 단위 적용.
//@Rollback
public class TransactionTest {
    @Autowired
    private UserService testUserService;
    private List<User> users;

    @BeforeEach
    void setup(){
        users = Arrays.asList(
                new User("1L", "Lionel Messi", "1234", Level.BASIC, 5, 10, "topdribleguy@naver.com"),
                new User("2C", "Christiano Ronaldo", "4321", Level.SILVER, 40, 100, "ipado@naver.com"));
    }

    @Test
    public void readOnlyTransactionAttribute(){
        testUserService.add(users.get(0));
        assertThrows(TransientDataAccessResourceException.class, () -> testUserService.getAll());
        // TransientDataAccessResourceException은 일시적인 예외상황을 만났을 때의 예외를 뜻한다.
        // 일시적이란 말 그대로 재시도 시 성공할 수도 있다(정상적으론 문제 없다)는 뜻으로, readOnly 속성으로 인한 일시적 실패를 의미.
        testUserService.deleteAll();
    }

    @Test
    @Transactional // 해당 테스트에 트랜잭션 경계설정을 한다. 또 테스트가 끝나면 자동으로 롤백해준다.
    @Rollback // 테스트 후 롤백을 해줄지 설정해줄 수 있다. 기본값은 true(롤백 함).
    public void transactionSync(){
        testUserService.deleteAll();
        testUserService.add(users.get(0));
        testUserService.add(users.get(1));
    }
}
