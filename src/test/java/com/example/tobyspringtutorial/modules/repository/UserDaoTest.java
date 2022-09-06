package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DaoFactory.class) // 통합 테스트
public class UserDaoTest {
    //@Autowired
    //private ApplicationContext ac;
    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void cleanData(){
        /*System.out.println(this.ac); // ac는 한번 만들어지고 계속 사용된다.
        System.out.println(this); // 테스트 오브젝트는 테스트 수행때마다 다시 생성된다.*/

        //this.dao = ac.getBean("userDao", UserDao.class);
        this.user1 = new User("1234", "HwangHeeChan", "13", Level.BASIC, 1, 0);
        this.user2 = new User("2", "SonHeungMin", "7", Level.SILVER, 55, 10);
        this.user3 = new User("3", "KimMinJae", "3", Level.GOLD, 100, 40);

        dao.deleteAll();
    }

    @Test
    void addAndGet() {
        dao.add(user1);
        dao.add(user2);

        User findUser1 = dao.get(user1.getId());
        checkSameUser(findUser1, user1);

        User findUser2 = dao.get(user2.getId());
        checkSameUser(findUser2, user2);
    }

    @Test
    public void getUserFailure() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("231421"));
    }

    @Test
    public void count() {
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);

    }

    @Test
    void getAll() {
        List<User> users0 = dao.getAll(); // Negative Test (비정상적인 결과가 도출되도록 하는 테스트. Positive Test보다 더 중요함. <-> Positive Test)
        assertThat(users0.size()).isEqualTo(0);

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User u1, User u2){
        assertThat(u1.getId()).isEqualTo(u2.getId());
        assertThat(u1.getUserName()).isEqualTo(u2.getUserName());
        assertThat(u1.getPassword()).isEqualTo(u2.getPassword());
        assertThat(u1.getLevel()).isEqualTo(u2.getLevel());
        assertThat(u1.getLogin()).isEqualTo(u2.getLogin());
        assertThat(u1.getRecommend()).isEqualTo(u2.getRecommend());
    }

    @Test
    void duplicateKey(){ // 스프링의 데이터 액세스 예외를 확인하는 학습 테스트
        dao.add(user1);

        assertThrows(DataAccessException.class, () -> dao.add(user1));
        assertThrows(DataIntegrityViolationException.class, () -> dao.add(user1));
        assertThrows(DuplicateKeyException.class, () -> dao.add(user1));
        // 아래로 갈수록 구체화된 예외의 테스트 코드다.
        // DuplicateKeyException은 JDBC에만 해당하여서 JPA 같은 다른 ORM에서는 이를 적용할 수 없다.
        // 이를테면 하이버네이트에서는 중복 키에 대해 DataIntegrityViolationException 예외를 발생시킨다.
        // 따라서 학습 테스트로 어떤 예외로 전환되는지 알 필요가 있다.
    }

    @Test
    void SqlExceptionTranslate() { // SQL DB 에러 코드를 이용해 예외 전환 시키는 테스트
        try {
            dao.add(user1);
            dao.add(user1);
        }catch (DuplicateKeyException e){
            SQLException sqlEx = (SQLException) e.getRootCause(); // 중첩된 예외를 가져온다.
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

            assertThat(set.translate(null, null, sqlEx)).isInstanceOf(DuplicateKeyException.class);
        }

    } // 이 테스트의 결과로 알 수 있듯, 어느 DB를 쓰더라도 우리가 알고 있는 예외로 전환을 할 수가 있다.

    @Test
    public void update(){
        // given
        dao.add(user1);
        dao.add(user2);

        // 아래부턴 인스턴스 변수를 수정하는 코드들인데, 어차피 테스트 실행마다 테스트 오브젝트가 새로 생성되니 수정해도 괜찮다.
        user1.setUserName("LeeKangIn");
        user1.setPassword("mallorca");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);
        // when
        User updateUser1 = dao.get(user1.getId());
        User sameUser2 = dao.get(user2.getId());
        // then
        checkSameUser(user1, updateUser1);
        checkSameUser(user2, sameUser2);
    }
}
