package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.List;

// 서비스 패키지는 주로 비즈니스 로직이 오는 패키지이다.
public class UserService{
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50; // 상수 중복 방지.
    public static final int MIN_RECCOUNT_FOR_GOLD = 30;
    protected UserDao userDao;
    private DataSource dataSource; // 트랜잭션 동기화 방식 적용을 위한 DataSource
    protected UserServicePolicy userServicePolicy; // 업그레이드 정책 변경 가능성을 염두한 정책 분리.

    public void setDataSource(DataSource dataSource) { // Connection을 생성할 때 사용할 DataSource를 DI.
        this.dataSource = dataSource;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserServicePolicy(UserServicePolicy userServicePolicy){
        this.userServicePolicy = userServicePolicy;
    }

    public void upgradeLevels() {
        // 스프링이 제공하는 트랜잭션 추상화 방법.
        // https://velog.io/@jakeseo_me/%ED%86%A0%EB%B9%84%EC%9D%98-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%A0%95%EB%A6%AC-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-5.2-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%B6%94%EC%83%81%ED%99%94
        PlatformTransactionManager tm // 트랜잭션 경계설정 추상 인터페이스. 역시 트랜잭션 동기화 저장소를 이용한다.
                = new DataSourceTransactionManager(dataSource); // JDBC를 위한 구현체
        TransactionStatus status = tm.getTransaction(new DefaultTransactionDefinition());
        // DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 지니고 있음.
        // 이렇게 시작된 트랜잭션은 TransactionStatus 타입 변수에 저장된다.

        try {
            List<User> users = userDao.getAll();
            for (User user : users)
                if (userServicePolicy.canUpgradeLevel(user))
                    userServicePolicy.upgradeLevel(user);
            tm.commit(status);
        }catch (Exception e){
            tm.rollback(status);
            throw e;
        }

    }

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
