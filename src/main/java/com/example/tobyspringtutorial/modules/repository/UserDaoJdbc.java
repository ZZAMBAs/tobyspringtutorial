package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.modules.objects.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class UserDaoJdbc implements UserDao{
    private RowMapper<User> userRowMapper;
    private JdbcTemplate jdbcTemplate; // 스프링서 직접 지원하는 JDBC 코드용 기본 템플릿. 직접 만든 jdbcContext와 역할이 동일.
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html

    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setUserRowMapper(RowMapper userRowMapper){ // DI
        this.userRowMapper = userRowMapper;
    }

    public void add(User user) throws DuplicateKeyException { // JdbcTemplate는 중복 키 삽입에 대한 예외를 만들어 놓았다.
        this.jdbcTemplate.update("insert into users values (?, ?, ?, ?, ?, ?, ?)",
                user.getId(), user.getUserName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
        // 바로 쿼리에 바인딩할 값을 넣어줄 수 있다.
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?", userRowMapper, id); // RowMapper는 ResultSet 인자와 인스턴스 사이 바인딩을 위한 콜백. 마지막 파라미터 id는 sql의 ? 바인딩을 위함.
        // queryForObject는 SQL 실행시 하나의 row 값만 얻기를 기대하여 ResultSet의 next()를 바로 실행한 뒤 RowMapper 콜백을 호출한다.
        // queryForObject는 ResultSet에 결과행이 1개일 때 쓰는것이 좋다.
    }

    public List<User> getAll(){
        return jdbcTemplate.query("select * from users order by id", userRowMapper);
        // query()는 T, List<T>, void 등으로 반환타입이 다양하다. 콜백에 따라 이는 결정된다.
    }

    public void deleteAll(){
        jdbcTemplate.update("delete from users");
    }


    public int getCount() {
        return this.jdbcTemplate.query(con -> con.prepareStatement("select count(*) from users"),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }); // createPreparedStatement() 메서드로 쿼리를 수행, 두번째 인자값인 ResultSetExtractor 인터페이스 내 extraData
        // 메서드로 결과 값을 가져오며 최종적으로 update() 메서드는 그 결과 값을 반환한다.
        // 이 콜백 오브젝트 코드는 재사용성이 있어서 이미 JdbcTemplate 내에는 아래처럼 한 줄로 바꿀 수 있도록 지원한다.
        // return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update("update users set username = ?, password = ?, level = ?, login = ?, " +
                "recommend = ?, email = ? where id = ?", user.getUserName(), user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }
}
