package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

// 트랜잭션 관련 작업만 하고 나머지 실제 비즈니스 로직은 구현된 클래스에 위임.
// 타깃의 인터페이스를 구현하고 위임하는 코드 작성의 번거로움, 부가기능 코드 중복 가능성 등 때문에 프록시를 만드는 것이 번거롭다.
public class UserServiceTx implements UserService{
    private UserService userService; // 타겟 오브젝트
    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void add(User user) { // 메서드 구현과 위임
        userService.add(user);
    }

    @Override
    public void upgradeLevels() { // 메서드 구현
        // 부가 기능 수행
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try { // 여기까지 부가 기능 수행
            userService.upgradeLevels(); // 위임
            transactionManager.commit(status); // 여기서부터 다시 부가 기능 수행
        }catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
