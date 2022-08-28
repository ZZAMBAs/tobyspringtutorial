package com.example.tobyspringtutorial;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.User;
import com.example.tobyspringtutorial.modules.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;
import java.util.List;

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

        dao.deleteAll();
    }

    @Test
    void addAndGet() throws SQLException {
        dao.add(user1);
        dao.add(user2);

        User findUser1 = dao.get("1234");
        assertThat(findUser1.getUserName()).isEqualTo(user1.getUserName());
        assertThat(findUser1.getPassword()).isEqualTo(user1.getPassword());

        User findUser2 = dao.get("2");
        assertThat(findUser2.getUserName()).isEqualTo(user2.getUserName());
        assertThat(findUser2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void getUserFailure() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("231421"));
    }

    @Test
    public void count() throws Exception {
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);

    }

    @Test
    void getAll() throws SQLException {
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
    }
}
