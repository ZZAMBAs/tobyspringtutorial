package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

// 서비스 패키지는 주로 비즈니스 로직이 오는 패키지이다.
public class UserService{
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50; // 상수 중복 방지.
    public static final int MIN_RECCOUNT_FOR_GOLD = 30;
    protected UserDao userDao;
    private PlatformTransactionManager transactionManager;
    protected UserServicePolicy userServicePolicy; // 업그레이드 정책 변경 가능성을 염두한 정책 분리.

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserServicePolicy(UserServicePolicy userServicePolicy){
        this.userServicePolicy = userServicePolicy;
    }

    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        // DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 지니고 있음.
        // 이렇게 시작된 트랜잭션은 TransactionStatus 타입 변수에 저장된다.

        try {
            upgradeLevelsInternal(); // 코드 간결화.
            this.transactionManager.commit(status);
        }catch (Exception e){
            this.transactionManager.rollback(status);
            throw e;
        }

    }

    private void upgradeLevelsInternal() {
        List<User> users = userDao.getAll();
        for (User user : users)
            if (userServicePolicy.canUpgradeLevel(user))
                userServicePolicy.upgradeLevel(user);
    }

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
