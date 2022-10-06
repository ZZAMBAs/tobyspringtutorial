package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DaoFactory.class)
public class TransactionTest {
    @Autowired
    private UserService testUserService;

    @Test
    public void readOnlyTransactionAttribute(){
        assertThrows(TransientDataAccessResourceException.class, () -> testUserService.getAll());
        // TransientDataAccessResourceException은 일시적인 예외상황을 만났을 때의 예외를 뜻한다.
        // 일시적이란 말 그대로 재시도 시 성공할 수도 있다(정상적으론 문제 없다)는 뜻으로, readOnly 속성으로 인한 일시적 실패를 의미.
    }
}
