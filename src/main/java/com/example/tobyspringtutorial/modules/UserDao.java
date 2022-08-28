package com.example.tobyspringtutorial.modules;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {
    private JdbcTemplate jdbcTemplate; // 스프링서 직접 지원하는 JDBC 코드용 기본 템플릿. 직접 만든 jdbcContext와 역할이 동일.

    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) throws SQLException {
        this.jdbcTemplate.update("insert into users values (?, ?, ?)",
                user.getId(), user.getUserName(), user.getPassword());
        // 바로 쿼리에 바인딩할 값을 넣어줄 수 있다.
    }

    public User get(String id) throws SQLException {
        /*Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if(!rs.next())
            throw new EmptyResultDataAccessException(1);
        User user = new User(rs.getString("id"), rs.getString("username"), rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;*/
        return jdbcTemplate.queryForObject("select * from users where id = ?", new RowMapper<>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                // rs.next(); 할 필요 없다. 이유는 아래 서술.
                return new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));
                // rs.getString 인자를 columnIndex로 넣어도 상관없다.
            }
        }, id); // RowMapper는 ResultSet 인자와 인스턴스 사이 바인딩을 위한 콜백. 마지막 파라미터 id는 sql의 ? 바인딩을 위함.
        // queryForObject는 SQL 실행시 하나의 row 값만 얻기를 기대하여 ResultSet의 next()를 바로 실행한 뒤 RowMapper 콜백을 호출한다.
        // queryForObject는 ResultSet에 결과행이 1개일 때 쓰는것이 좋다.
    }

    public List<User> getAll(){
        return jdbcTemplate.query("select * from users order by id", (rs, rowNum) -> { // RowMapper
            return new User(rs.getString(1), rs.getString(2), rs.getString(3));
        });
        // query()는 T, List<T>, void 등으로 반환타입이 다양하다. 콜백에 따라 이는 결정된다.
    }

    public void deleteAll(){
        /*jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("delete from users");
            }
        });*/
        // jdbcTemplate 에서 PreparedStatementCreator 인터페이스의 createPreparedStatement() 메서드가 콜백이다.
        // 해당 콜백을 받아 업데이트하는 템플릿 메서드는 update() 메서드다. 위처럼 전략을 따로 생성할 수도 있고 아래처럼 SQL만 넘길 수도 있다.
        jdbcTemplate.update("delete from users");
    }


    public int getCount() throws SQLException {
        return this.jdbcTemplate.query(con -> con.prepareStatement("select count(*) from users"),
                new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    rs.next();
                    return rs.getInt(1);
                }
            }); // createPreparedStatement() 메서드로 쿼리를 수행, 두번째 인자값인 ResultSetExtractor 인터페이스 내 extraData
        // 메서드로 결과 값을 가져오며 최종적으로 update() 메서드는 그 결과 값을 반환한다.
        // 이 콜백 오브젝트 코드는 재사용성이 있어서 이미 JdbcTemplate 내에는 아래처럼 한 줄로 바꿀 수 있도록 지원한다.
        // return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

}
