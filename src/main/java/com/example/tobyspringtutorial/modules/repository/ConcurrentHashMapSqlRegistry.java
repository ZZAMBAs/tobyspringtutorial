package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;
import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 동시성 문제를 해결하기 위한, 업데이트 가능한 SQL 레지스트리.
public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry{
    private final ConcurrentHashMap<String, String> sqlMap = new ConcurrentHashMap<>();

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if (sql == null) throw new SqlNotFoundException(key + "를 이용하여 SQL을 찾을 수 없습니다.");
        return sql;
    }

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        if (sqlMap.get(key) == null) throw new SqlUpdateFailureException(key + "에 해당하는 SQL이 없습니다.");
        sqlMap.put(key, sql);
    }

    @Override
    public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
        for (Map.Entry<String, String> entry : sqlMap.entrySet())
            updateSql(entry.getKey(), entry.getValue());
    }
}
