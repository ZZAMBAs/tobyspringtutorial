package com.example.tobyspringtutorial.modules;

import com.example.tobyspringtutorial.forTest.forFactoryBean.MessageFactoryBean;
import com.example.tobyspringtutorial.forTest.service.TestUserService;
import com.example.tobyspringtutorial.forTest.service.TestUserServicePolicy;
import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.EmbeddedDbSqlRegistry;
import com.example.tobyspringtutorial.modules.repository.SqlRegistry;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import com.example.tobyspringtutorial.modules.repository.UserDaoJdbc;
import com.example.tobyspringtutorial.modules.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@Configuration
@EnableTransactionManagement //@Transactional가 붙은 것을 추적하는 포인트컷과 해당 애노테이션 내 트랜잭션 속성을 이용하는 어드바이스를 이용한 어드바이저를 적용한다.
// https://box0830.tistory.com/230
public class DaoFactory {
    @Bean
    public UserDao userDao(){
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        //userDaoJdbc.setDataSource(dataSource()); // 자동와이어링으로 주입 됨.
        //userDaoJdbc.setDataSource(embeddedDatabase()); // 현재 트랜잭션이 적용되지 않는 에러 존재.
        userDaoJdbc.setUserRowMapper(userRowMapper()); // 이것들처럼 나머지 setter로 된 빈 주입 DI들도 자동와이어링화 할 수 있다.
        userDaoJdbc.setSqlService(sqlService());
        return userDaoJdbc;
    }

    @Bean
    public SqlService sqlService(){
        DefaultSqlService sqlService = new DefaultSqlService(); // 디폴트 의존관계 빈을 사용.
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry(){
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
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

    /*@Bean(destroyMethod = "shutdown")
    public EmbeddedDatabaseFactoryBean embeddedDatabaseFactoryBean(){
        EmbeddedDatabaseFactoryBean embeddedDatabaseFactoryBean = new EmbeddedDatabaseFactoryBean();
        embeddedDatabaseFactoryBean.setInitSqlResourcePath("classpath:schema.sql", "classpath:initSqlSchema.sql");
        return embeddedDatabaseFactoryBean;
    }*/

    @Bean
    public DataSource embeddedDatabase(){
        return new EmbeddedDatabaseBuilder().setName("embeddedDatabase")
            .setType(H2).addScript("classpath:initSqlSchema.sql").addScript("classpath:schema.sql").build();
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
        return new DataSourceTransactionManager(dataSource()); // 기본 JDBC를 이용할 경우
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
    public UserService userService(){
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

    // 테스트 용. 포인트 컷에 맞추어 이름 수정.
    @Bean
    public UserService testUserService(){
        TestUserService testUserService = new TestUserService();
        testUserService.setUserDao(userDao());
        TestUserServicePolicy testUserServicePolicy = new TestUserServicePolicy("4MR");
        testUserServicePolicy.setUserDao(userDao());
        testUserServicePolicy.setMailSender(mailSender());
        testUserService.setUserServicePolicy(testUserServicePolicy);
        return testUserService;
    }
}
