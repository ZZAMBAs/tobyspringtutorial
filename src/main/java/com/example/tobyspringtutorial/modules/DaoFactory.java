package com.example.tobyspringtutorial.modules;

import com.example.tobyspringtutorial.forTest.forFactoryBean.MessageFactoryBean;
import com.example.tobyspringtutorial.forTest.service.TestUserService;
import com.example.tobyspringtutorial.forTest.service.TestUserServicePolicy;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import com.example.tobyspringtutorial.modules.repository.UserDaoJdbc;
import com.example.tobyspringtutorial.modules.service.DummyMailSender;
import com.example.tobyspringtutorial.modules.service.UserServiceImpl;
import com.example.tobyspringtutorial.modules.service.UserServicePolicy;
import com.example.tobyspringtutorial.modules.service.UserServicePolicyDefault;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

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

    // 트랜잭션을 위한 어드바이스를 빈으로 등록. 스프링이 지원하는 TransactionInterceptor를 사용한 것으로 리팩토링.
    // 이것에 대한 학습 테스트를 TransactionTest에서 확인.
    @Bean
    public TransactionInterceptor transactionAdvice(){
        TransactionInterceptor transactionAdvice = new TransactionInterceptor();
        transactionAdvice.setTransactionManager(transactionManager());
        Properties transactionAttributes = new Properties();
        transactionAttributes.setProperty("get*", "PROPAGATION_REQUIRED, readOnly, timeout_30");
        transactionAttributes.setProperty("upgrade*", "PROPAGATION_REQUIRES_NEW, ISOLATION_SERIALIZABLE");
        transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED");
        // 트랜잭션 격리 수준: https://velog.io/@guswns3371/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EA%B2%A9%EB%A6%AC%EC%88%98%EC%A4%80
        // https://zangzangs.tistory.com/167 https://code-lab1.tistory.com/52
        // 메서드 이름이 하나 이상의 패턴과 일치하는 경우, 매서드 패턴의 일치가 더 자세한 것을 따른다.
        transactionAdvice.setTransactionAttributes(transactionAttributes);
        return transactionAdvice;
    }

    // 트랜잭션을 적용할 포인트 컷. 모든 메서드에 적용되도록 빈 이름으로 리팩토링.
    // 사실 트랜잭션은 적용되는 클래스 내에서 전부 적용하도록 일반화하는 것이 혼란이 적다.
    // 일반화하기 적당하지 않다면 별도의 어드바이스와 포인트컷을 적용하자.
    // 포인트컷 표현식은 타입 패턴(execution)이나 빈 이름(아이디)(bean)을 이용한다.
    @Bean
    public AspectJExpressionPointcut transactionPointcut(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("bean(*Service)");
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
        TestUserService testUserService = new TestUserService();
        testUserService.setUserDao(userDao());
        TestUserServicePolicy testUserServicePolicy = new TestUserServicePolicy("4MR");
        testUserServicePolicy.setUserDao(userDao());
        testUserServicePolicy.setMailSender(mailSender());
        testUserService.setUserServicePolicy(testUserServicePolicy);
        return testUserService;
    }

    // 어드바이저를 이용하는 자동 프록시 생성기.
    // Advisor 인터페이스를 구현한 빈을 전부 찾은 뒤, 생성되는 모든 빈에 대해 어드바이저의 포인트 컷을 적용해보며 프록시 적용 대상을 선정한다.
    // 프록시 적용 대상이라면 프록시를 만들어 해당 빈을 대체하도록 한다. 따라서 이런 타겟 빈에 의존하는 다른 빈들은 해당 프록시를 DI 받게된다.
    @Bean
    @DependsOn("transactionAdvisor") // DependsOn 어노테이션으로 해당 빈에 의존함을 명시할 경우, 해당 빈을 현재 빈보다 먼저 생성한다.
    // 어드바이저가 먼저 필요하므로 이렇게 설정한다.
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

}
