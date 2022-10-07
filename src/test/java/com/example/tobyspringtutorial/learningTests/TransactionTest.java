package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import com.example.tobyspringtutorial.modules.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DaoFactory.class)
public class TransactionTest {
    @Autowired
    private UserService testUserService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private UserDao userDao;
    private List<User> users;

    @BeforeEach
    void setup(){
        users = Arrays.asList(
                new User("1L", "Lionel Messi", "1234", Level.BASIC, 5, 10, "topdribleguy@naver.com"),
                new User("2C", "Christiano Ronaldo", "4321", Level.SILVER, 40, 100, "ipado@naver.com"));
    }

    @Test
    public void readOnlyTransactionAttribute(){
        assertThrows(TransientDataAccessResourceException.class, () -> testUserService.getAll());
        // TransientDataAccessResourceException은 일시적인 예외상황을 만났을 때의 예외를 뜻한다.
        // 일시적이란 말 그대로 재시도 시 성공할 수도 있다(정상적으론 문제 없다)는 뜻으로, readOnly 속성으로 인한 일시적 실패를 의미.
    }

    @Test
    public void transactionSync(){
        userDao.deleteAll();
        assertThat(userDao.getCount()).isEqualTo(0);

        // 트랜잭션을 미리 생성한다.
        // 트랜잭션의 PROPAGATION 속성 외 속성들은 최초 실행 시 트랜잭션 속성 값에 고정되고 끝날 때까지 변하지 않는다.
        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef);

        // 현재 아래 트랜잭션의 속성 값들은 PROPAGATION_REQUIRED 이므로, 이미 만들어진 트랜잭션에 합류한다.
        testUserService.add(users.get(0));
        testUserService.add(users.get(1));
        assertThat(userDao.getCount()).isEqualTo(2);

        transactionManager.rollback(status); // 모든 트랜잭션이 한꺼번에 롤백된다.

        assertThat(userDao.getCount()).isEqualTo(0);
    }
}
