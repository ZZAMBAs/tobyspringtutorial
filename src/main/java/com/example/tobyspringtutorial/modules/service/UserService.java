package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

    public void upgradeLevels() throws SQLException {
        TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자를 이용한 동기화 작업 초기화.
        // https://mangkyu.tistory.com/154
        Connection c = DataSourceUtils.getConnection(dataSource); // DB 커넥션을 생성 + 트랜잭션 저장소 동기화(저장).
        c.setAutoCommit(false); // 일반적으로 설정되어 있는 자동 커밋을 해제. 이러면 수동적으로 트랜잭션 제어가 가능하다.

        try {
            List<User> users = userDao.getAll();
            for (User user : users)
                if (userServicePolicy.canUpgradeLevel(user))
                    userServicePolicy.upgradeLevel(user);
            c.commit();
        }catch (Exception e){
            c.rollback();
            throw e;
        }
        finally {
            DataSourceUtils.releaseConnection(c, dataSource); // DB 커넥션을 안전하게 닫음.
            TransactionSynchronizationManager.unbindResource(this.dataSource); // 바인딩 제거
            TransactionSynchronizationManager.clearSynchronization(); // 동기화 작업 종료
        }
    } // 코드 성격 통합, 재사용성 코드 분리, 코드 가독성 향상 등의 이유로 리팩토링.

    public void add(User user){
        if (user.getLevel() == null) // 레벨은 NOT NULL 항목이다.
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
