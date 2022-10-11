package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;
import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry{
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource){ // ISP를 지키기 위해 EmbeddedDataSource 가 아닌 DataSource를 주입받도록 했다.
        // EmbeddedDataSource 는 DataSource + 종료 기능인데, 이 클래스에서는 종료기능은 사용하지 않는다. 따라서 DataSource로 주입받는다.
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public void registerSql(String key, String sql) {
        jdbcTemplate.update("INSERT INTO SQLMAP VALUES (?, ?)", key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        try{
            return jdbcTemplate.queryForObject("SELECT SQL_ FROM SQLMAP WHERE KEY_ = ?", String.class, key);
        }catch (EmptyResultDataAccessException e) { // jdbcTemplate는 SQL 결과값이 없으면 해당 예외를 내뱉는다.
            throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbcTemplate.update("UPDATE SQLMAP SET SQL_ = ? WHERE KEY_ = ?", sql, key);
        if (affected == 0) throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
    }

    @Override
    public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
        for (Map.Entry<String, String> entry : sqlMap.entrySet()){
            updateSql(entry.getKey(), entry.getValue());
        }
    }
}
