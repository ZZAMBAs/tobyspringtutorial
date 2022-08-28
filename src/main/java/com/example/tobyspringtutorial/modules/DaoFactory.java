package com.example.tobyspringtutorial.modules;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao(){
        //ConnectionMaker connectionMaker = connectionMaker();
        UserDao userDao = makeUserDao();
        userDao.setDataSource(dataSource());
        // userDao.setJdbcContext(jdbcContext()); // 스프링을 이용한 JdbcContext 주입
        return userDao;
    }

    @Bean
    public CountingConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() { // 중복 메서드 제거 위함.
        return new DConnectionMaker();
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
    public UserDao makeUserDao(){ // 템플릿 메서드 패턴에 의한 구체화.
        return new UserDaoDeleteAll();
    }

    /*@Bean
    public JdbcContext jdbcContext(){ // 스프링의 DI는 빈끼리만 가능하다! JdbcContext는 DataSource를 DI로 주입받고 있다.
        // 그래서 인터페이스가 아닌 구체 클래스인 JdbcContext도 스프링 빈으로 등록한다.
        // 이렇게 인터페이스가 없다면 모듈 사이 결합도가 높아진다.
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());
        return jdbcContext;
    }*/ // 스프링을 이용할 때의 코드
}
