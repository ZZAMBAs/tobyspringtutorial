package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.exception.TestUserServiceException;
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

import static com.example.tobyspringtutorial.modules.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.tobyspringtutorial.modules.service.UserService.MIN_RECCOUNT_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
                // 가변 인자를 상수 이름으로 넣어주어 테스트 의도를 알기 쉬워졌다.
                new User("1EH", "엘링 홀란", "p1", Level.BASIC,
                        MIN_LOGCOUNT_FOR_SILVER - 1, 0), // 경곗값 분석 테스트
                new User("2MS", "모하메드 살라", "p2", Level.BASIC,
                        MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("3SHM", "손흥민", "p3", Level.SILVER,
                        60, MIN_RECCOUNT_FOR_GOLD - 1),
                new User("4MR", "마커스 래시포드", "p4", Level.SILVER,
                        60, MIN_RECCOUNT_FOR_GOLD),
                new User("5KDB", "케빈 데브라위너", "p5", Level.GOLD,
                        100, Integer.MAX_VALUE)
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
        checkLevelUpgradeOccurred(users.get(0), false);
        checkLevelUpgradeOccurred(users.get(1), true);
        checkLevelUpgradeOccurred(users.get(2), false);
        checkLevelUpgradeOccurred(users.get(3), true);
        checkLevelUpgradeOccurred(users.get(4), false);
    }

    private void checkLevelUpgradeOccurred(User user, boolean expected){ // 리팩토링. 이제 2번째 파라미터는 업그레이드 되어야 하는지를 넘겨준다.
        User updatedUser = userDao.get(user.getId());
        if (expected)
            assertThat(updatedUser.getLevel()).isEqualTo(user.getLevel().nextLevel());
        else
            assertThat(updatedUser.getLevel()).isEqualTo(user.getLevel());
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
        User addedUserWithLevel = userDao.get(userWithLevel.getId());
        User addedUserWithoutLevel = userDao.get(userWithoutLevel.getId());
        assertThat(addedUserWithLevel.getLevel()).isEqualTo(userWithLevel.getLevel()); // 레벨 정보 있던 데이터는 변동 없어야 함.
        assertThat(addedUserWithoutLevel.getLevel()).isEqualTo(Level.BASIC); // 레벨 정보 없던 데이터는 BASIC으로 초기화 돼야함.
    }

    @Test
    public void upgradeAllOrNothing(){
        // given
        UserService testUserService = new UserServiceForExceptionTest(users.get(3).getId());
        testUserService.setUserDao(this.userDao); // 스프링 빈이 아니므로 수동 DI
        userDao.deleteAll();
        for (User user : users) userDao.add(user);
        // when
        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException Expected");
            // Assertions.fail() 메서드에 코드가 도달하면 테스트 실패. 즉 이 코드에 오기전에 예외가 catch 되어야한다.
        }catch (TestUserServiceException e){}
        // then
        checkLevelUpgradeOccurred(users.get(1), false); // 롤백 되었는지 확인
        // 트랜잭션 문제로 이 테스트는 실패한다.
    }
}