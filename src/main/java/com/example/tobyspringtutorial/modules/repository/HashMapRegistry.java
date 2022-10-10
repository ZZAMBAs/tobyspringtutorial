package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class HashMapRegistry implements SqlRegistry{
    private Map<String, String> sqlMap = new HashMap<>();

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if (sql == null) throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        return sql;
    }
}
