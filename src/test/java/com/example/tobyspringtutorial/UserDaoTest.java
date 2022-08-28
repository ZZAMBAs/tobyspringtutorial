package com.example.tobyspringtutorial;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.JdbcContext;
import com.example.tobyspringtutorial.modules.User;
import com.example.tobyspringtutorial.modules.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
// import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
// import org.springframework.test.context.ContextConfiguration; // JUnit4

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DaoFactory.class) // 통합 테스트
public class UserDaoTest {
    @Autowired
    private ApplicationContext ac;
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void cleanData() throws SQLException {
        /*System.out.println(this.ac); // ac는 한번 만들어지고 계속 사용된다.
        System.out.println(this); // 테스트 오브젝트는 테스트 수행때마다 다시 생성된다.*/

        this.dao = ac.getBean("userDao", UserDao.class);
        this.user1 = new User("1234", "HwangHeeChan", "13");
        this.user2 = new User("2", "SonHeungMin", "7");
        this.user3 = new User("3", "KimMinJae", "3");

        dao.deleteAllbyS_TC();
    }

    @Test
    void addAndGet() throws SQLException {
        dao.addByS_localClassUsed(user1);
        dao.addByS_localClassUsed(user2);

        User findUser1 = dao.get("1234");
        assertThat(findUser1.getUserName()).isEqualTo(user1.getUserName());
        assertThat(findUser1.getPassword()).isEqualTo(user1.getPassword());

        User findUser2 = dao.get("2");
        assertThat(findUser2.getUserName()).isEqualTo(user2.getUserName());
        assertThat(findUser2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void getUserFailure() {
        // given
        // when
        // then
        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("231421"));
    }

    @Test
    public void count() throws Exception {
        assertThat(dao.getCount()).isEqualTo(0);

        dao.addByS_TC(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.addByS_TC(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.addByS_TC(user3);
        assertThat(dao.getCount()).isEqualTo(3);

    }
}
