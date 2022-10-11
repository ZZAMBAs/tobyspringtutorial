package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;

import java.util.Map;

// SQL 실시간 업데이트를 위한 인터페이스. SqlRegistry의 등록, 검색 기능을 포함하여 가지고 있다.
public interface UpdatableSqlRegistry extends SqlRegistry{
    void updateSql(String key, String sql) throws SqlUpdateFailureException;
    void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException;
}
