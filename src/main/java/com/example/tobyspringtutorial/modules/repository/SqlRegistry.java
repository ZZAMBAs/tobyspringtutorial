package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;

public interface SqlRegistry { // SQL 저장소 (애플리케이션 내)
    void registerSql(String key, String sql);
    String findSql(String key) throws SqlNotFoundException;
}
