package com.example.tobyspringtutorial.modules;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import com.example.tobyspringtutorial.modules.repository.UserDaoJdbc;
import com.example.tobyspringtutorial.modules.service.*;
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
    public UserService userService(){
        // Client -> UserServiceTx -> UserServiceImpl 순. 트랜잭션 처리가 비즈니스 로직 처리보다 앞선다.
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setUserService(userServiceImpl());
        userServiceTx.setTransactionManager(transactionManager());
        return userServiceTx;
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
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserDao(userDao());
        userServiceImpl.setUserServicePolicy(userServicePolicy());
        return userServiceImpl;
    }
}
