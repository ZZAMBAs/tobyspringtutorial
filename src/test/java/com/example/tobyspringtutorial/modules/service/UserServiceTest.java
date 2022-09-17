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
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.tobyspringtutorial.modules.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.tobyspringtutorial.modules.service.UserServiceImpl.MIN_RECCOUNT_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(classes = DaoFactory.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    @Autowired // 빈의 자동 주입은 일단 오브젝트 타입을 보고, 동일 타입 빈이 2개 이상이면 빈 이름과 필드 이름이 일치하는 것을 주입한다.
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
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
    @DirtiesContext // DI를 바꿔야 되는 경우 표기하는 어노테이션. 이 어노테이션으로 인해 재활용하던 빈을 테스트 후 재생성한다.
    // https://shortstories.gitbooks.io/studybook/content/dirtiescontext.html
    public void upgradeLevels() {
        // given
        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        UserServicePolicyDefault testPolicy = new UserServicePolicyDefault();
        testPolicy.setUserDao(this.userDao);
        testPolicy.setMailSender(mockMailSender); // 기존 DummyMailSender는 정보를 저장할 수 없어 여기서 테스트하기 힘들었다.
        userServiceImpl.setUserServicePolicy(testPolicy);

        // when
        userService.upgradeLevels();
        checkLevelUpgradeOccurred(users.get(0), false);
        checkLevelUpgradeOccurred(users.get(1), true);
        checkLevelUpgradeOccurred(users.get(2), false);
        checkLevelUpgradeOccurred(users.get(3), true);
        checkLevelUpgradeOccurred(users.get(4), false); // 위 결과들(성공 시 이메일 저장)은 목 오브젝트 내에 저장된다.

        // then
        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
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
    public void upgradeAllOrNothing() {
        // given
        String testId = users.get(3).getId();
        UserServiceImpl testUserServiceImpl = new UserServiceForExceptionTest();
        testUserServiceImpl.setUserDao(this.userDao); // 스프링 빈이 아니므로 수동 DI

        UserServicePolicyForExceptionTest testUserServicePolicy = new UserServicePolicyForExceptionTest(testId);
        testUserServicePolicy.setMailSender(this.mailSender);
        testUserServicePolicy.setUserDao(this.userDao);
        testUserServiceImpl.setUserServicePolicy(testUserServicePolicy);

        UserServiceTx testUserServiceTx = new UserServiceTx();
        testUserServiceTx.setTransactionManager(this.transactionManager);
        testUserServiceTx.setUserService(testUserServiceImpl);

        userDao.deleteAll();
        for (User user : users) userDao.add(user);
        // when
        try {
            testUserServiceTx.upgradeLevels();
            fail("TestUserServiceException Expected");
            // Assertions.fail() 메서드에 코드가 도달하면 테스트 실패. 즉 이 코드에 오기전에 예외가 catch 되어야한다.
        }catch (TestUserServiceException e){}
        // then
        checkLevelUpgradeOccurred(users.get(1), false); // 롤백 되었는지 확인
        // 트랜잭션 동기화로 이 테스트는 성공한다.
    }

    @Test
    public void mailSenderTest(){ // 콘솔 출력으로 테스트가 잘 되었는지 확인.
        // given
        User testUser = new User("12345", "조인스", "qwerty",
                Level.BASIC, 1000, 999, "rksidksrksi@naver.com");
        UserServicePolicyForExceptionTest testPolicy = new UserServicePolicyForExceptionTest("2");
        testPolicy.setUserDao(this.userDao);
        testPolicy.setMailSender(this.mailSender);
        userDao.deleteAll();
        userDao.add(testUser);
        // when
        testPolicy.upgradeLevel(testUser);
        // then
    }

    static class MockMailSender implements MailSender{ // UserServiceTest 클래스 내에서만 사용할 정적 클래스(목 오브젝트)
        // DummyMailSender(스텁)과 비슷하나, 목 오브젝트는 관련 정보를 저장한다.
        private final List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            requests.add(simpleMessage.getTo()[0]); // 전송 요청을 받은 이메일 주소를 저장. 여기서는 간단히 첫번째 수신자만 저장.
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {

        }
    }

    // 아래 클래스들은 upgradeAllOrNothing 테스트를 위해서만 사용한다.
    static class UserServiceForExceptionTest extends UserServiceImpl {
        public UserServiceForExceptionTest() {
        }
    }

    static class UserServicePolicyForExceptionTest extends UserServicePolicyDefault{
        private final String id;

        public UserServicePolicyForExceptionTest(String id) {
            this.id = id;
        }

        @Override
        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

}