package com.example.tobyspringtutorial.modules;

import com.example.tobyspringtutorial.forTest.forFactoryBean.MessageFactoryBean;
import com.example.tobyspringtutorial.forTest.service.TestUserServicePolicy;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import com.example.tobyspringtutorial.modules.repository.UserDaoJdbc;
import com.example.tobyspringtutorial.modules.service.*;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao(){
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setDataSource(dataSource());
        userDaoJdbc.setUserRowMapper(userRowMapper());
        return userDaoJdbc;
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class); // 최근 바뀐 드라이버 클래스.
        // gradle 빌드 도구를 사용하므로, 외부 jar 파일을 넣으면 안됨.
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

    @Bean
    public RowMapper<User> userRowMapper(){
        // 재사용성이 많아 UserDao에선 인스턴스 변수로 초기화하고, 이것이 따로 상태 필드가 없으며 한번만 뽑으면 되므로, 빈으로 만듦.
        return (rs, rowNum) -> new User(rs.getString("id"),
                rs.getString("username"),
                rs.getString("password"),
                Level.valueOf(rs.getInt("level")),
                rs.getInt("login"),
                rs.getInt("recommend"),
                rs.getString("email"));
    }

    @Bean
    public UserServicePolicy userServicePolicy(){ // 업그레이드 정책
        UserServicePolicyDefault userServicePolicy = new UserServicePolicyDefault();
        userServicePolicy.setUserDao(userDao());
        userServicePolicy.setMailSender(mailSender());
        return userServicePolicy;
    }

    @Bean // 다른 곳에서도 사용 가능성이 높고 싱글톤 사용이 가능하여 빈으로 등록
    public PlatformTransactionManager transactionManager(){
        // Javadoc: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html
        return new DataSourceTransactionManager(this.dataSource()); // 기본 JDBC를 이용할 경우
        // return new JtaTransactionManager(); // JTA를 이용할 경우
        // return new JpaTransactionManager(); // JPA를 이용할 경우
        // return new HibernateTransactionManager(); // Hibernate를 이용할 경우
        // 이외에도 많음.
    }

    @Bean
    public MailSender mailSender(){
        DummyMailSender mailSender = new DummyMailSender();// 구체적으로 사용할 MailSender 정의
        // mailSender.setHost("mail.server.com"); // 메일 서버 지정
        return mailSender;
    }

    @Bean
    public UserServiceImpl userService(){
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserDao(userDao());
        userServiceImpl.setUserServicePolicy(userServicePolicy());
        return userServiceImpl;
    }

    // 학습 테스트용
    @Bean(name = "message") // MessageFactoryBean 클래스 참조
    public MessageFactoryBean messageFactoryBean(){
        MessageFactoryBean messageFactoryBean = new MessageFactoryBean();
        messageFactoryBean.setText("Factory Bean");
        return messageFactoryBean;
    }

    // 트랜잭션을 위한 어드바이스를 빈으로 등록
    @Bean
    public TransactionAdvice transactionAdvice(){
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }

    // 트랜잭션을 적용할 포인트 컷. 클래스 선정 알고리즘까지 포함하여 리팩토링 하였음.
    @Bean
    public NameMatchMethodPointcut transactionPointcut(){
        NameMatchClassMethodPointcut pointcut = new NameMatchClassMethodPointcut();
        pointcut.setMappedClassName("*ServiceImpl");
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    // 트랜잭션을 위한 어드바이저. 자동 프록시 생성기를 사용함에 따라 이것을 명시적으로 DI 받는 빈은 이제 없다.
    @Bean
    public DefaultPointcutAdvisor transactionAdvisor(){
        return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvice());
    }

    // 테스트 용. 포인트 컷에 맞추어 이름 수정.
    @Bean
    public UserServiceImpl testUserService(){
        UserServiceImpl testUserService = new UserServiceImpl();
        testUserService.setUserDao(userDao());
        testUserService.setUserServicePolicy(testUserServicePolicy());
        return testUserService;
    }

    // 테스트 용.
    @Bean
    public UserServicePolicy testUserServicePolicy(){
        TestUserServicePolicy testUserServicePolicy = new TestUserServicePolicy("4MR");
        testUserServicePolicy.setUserDao(userDao());
        testUserServicePolicy.setMailSender(mailSender());
        return testUserServicePolicy;
    }

    // 어드바이저를 이용하는 자동 프록시 생성기.
    // Advisor 인터페이스를 구현한 빈을 전부 찾은 뒤, 생성되는 모든 빈에 대해 어드바이저의 포인트 컷을 적용해보며 프록시 적용 대상을 선정한다.
    // 프록시 적용 대상이라면 프록시를 만들어 해당 빈을 대체하도록 한다. 따라서 이런 타겟 빈에 의존하는 다른 빈들은 해당 프록시를 DI 받게된다.
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

}
