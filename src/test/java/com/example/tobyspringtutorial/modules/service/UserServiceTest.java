package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DaoFactory.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    List<User> users;

    @BeforeAll
    public void setUp(){
        users = Arrays.asList( // 배열을 리스트로 만들어주는 편리한 메서드. 배열을 가변 인자로 넣어주면 더욱 편리.
                // 한글 깨질 시, 인코딩을 모두 UTF-8로 해준다. DB 설정도 필수.
                new User("EH", "엘링 홀란", "p1", Level.BASIC, 49, 0), // 경곗값 분석 테스트
                new User("MS", "모하메드 살라", "p2", Level.BASIC, 50, 0),
                new User("SHM", "손흥민", "p3", Level.SILVER, 60, 29),
                new User("MR", "마커스 래시포드", "p4", Level.SILVER, 60, 30),
                new User("KDB", "케빈 데브라위너", "p5", Level.GOLD, 100, 100)
        );
    }

    @Test
    public void upgradeLevels(){
        // given
        userDao.deleteAll();
        for (User user : users) userDao.add(user);
        // when
        userService.upgradeLevels();
        // then
        checkLevels(users.get(0), Level.BASIC);
        checkLevels(users.get(1), Level.SILVER);
        checkLevels(users.get(2), Level.SILVER);
        checkLevels(users.get(3), Level.GOLD);
        checkLevels(users.get(4), Level.GOLD);
    }

    void checkLevels(User user, Level level){
        User updatedUser = userDao.get(user.getId());
        assertThat(updatedUser.getLevel()).isEqualTo(level);
    }

    @Test
    public void add(){
        // given
        userDao.deleteAll();
        User userWithLevel = users.get(4); // GOLD 레벨. 이미 레벨이 지정되어 있음.
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);
        // when
        userService.add(userWithLevel);
        userService.add(userWithoutLevel);
        // then
        checkLevels(userDao.get(userWithLevel.getId()), userWithLevel.getLevel()); // 레벨 정보 있던 데이터는 변동 없어야 함.
        checkLevels(userDao.get(userWithoutLevel.getId()), Level.BASIC); // 레벨 정보 없던 데이터는 BASIC으로 초기화 돼야함.
    }
}