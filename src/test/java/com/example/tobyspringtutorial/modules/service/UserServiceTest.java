package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.DaoFactory;
import com.example.tobyspringtutorial.modules.exception.TestUserServiceException;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

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
    @DirtiesContext
    public void upgradeLevels() { // 단위 테스트로써 재정의. 의존 관계인 모든 모듈을 목 오브젝트 또는 스텁으로 대체한다.
        // 단위 테스트는 다른 의존 모듈을 가져오지 않기에 테스트 실행 속도가 더 빨라진다.
        // given
        UserServiceImpl userServiceImpl = new UserServiceImpl(); // 단위 테스트는 테스트 대상 오브젝트를 직접 생성한다.
        MockMailSender mockMailSender = new MockMailSender();
        MockUserDao mockUserDao = new MockUserDao(this.users);

        UserServicePolicyDefault testPolicy = new UserServicePolicyDefault();
        testPolicy.setUserDao(mockUserDao);
        testPolicy.setMailSender(mockMailSender);

        userServiceImpl.setUserDao(mockUserDao);
        userServiceImpl.setUserServicePolicy(testPolicy);
        // when
        userServiceImpl.upgradeLevels();
        // then
        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2); // 업데이트가 2번만 일어났는지 검증한다.
        checkUserAndLevel(updated.get(0), users.get(1).getId(), Level.SILVER); // 제대로 업데이트 되었는지 검증한다.
        checkUserAndLevel(updated.get(1), users.get(3).getId(), Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    @Test
    public void mockUpgradeLevels(){ // mockito 프레임워크를 이용한 upgradeLevels 테스트
        // mockito Javadoc: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#method.detail
        // 사용법: https://scshim.tistory.com/439
        // given
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class); // 해당 인터페이스의 목 오브젝트 자동 생성.
        when(mockUserDao.getAll()).thenReturn(this.users); // mockUserDao가 getAll() 메서드 실행 시 users 목록을 리턴하도록 설정.
        // mockito는 자동으로 호출 기록을 남긴다.
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        UserServicePolicyDefault testPolicy = new UserServicePolicyDefault();
        testPolicy.setUserDao(mockUserDao);
        testPolicy.setMailSender(mockMailSender);

        userServiceImpl.setUserServicePolicy(testPolicy);
        // when
        userServiceImpl.upgradeLevels();
        // then
        verify(mockUserDao, times(2)).update(any(User.class)); // mockito에서 검증하는 메서드.
        // any를 이용하면 파라미터 내용은 무시한다.
        verify(mockUserDao).update(users.get(1)); // 실제 파라미터 검사. users.get(1)을 파라미터로 update가 호출된 적 있는지 확인한다.
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        // ArgumentCaptor를 이용해 mockMailSender 오브젝트에 전달된 파라미터를 가져와 검증에 사용했다.
        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());

    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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


    static class MockUserDao implements UserDao{
        private final List<User> users; // 레벨 업그레이드 후보 User 오브젝트 목록
        private final List<User> updated = new ArrayList<>(); // 업그레이드 대상 User 오브젝트 저장 목록

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        // 스텁 기능 제공 (단순히 미리 준비된 목록을 제공할 경우)
        @Override
        public List<User> getAll() {
            return this.users;
        }

        // 목 오브젝트 기능 제공 (해당 메서드가 사용되었는지 기록 + 검증하기 위함)
        @Override
        public void update(User user) {
            updated.add(user);
        }

        // 아래는 테스트에 사용하지 않는 메서드들이다. 해당 예외를 던져 이를 사용하지 못하도록 하고 사용할 경우 개발자에게 알려주도록 한다.
        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }

        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }

        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }

        @Override
        public int getCount() { throw new UnsupportedOperationException(); }
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