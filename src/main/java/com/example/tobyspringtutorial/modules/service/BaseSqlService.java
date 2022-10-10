package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;
import com.example.tobyspringtutorial.exceptions.SqlRetrievalFailureException;
import com.example.tobyspringtutorial.modules.repository.SqlReader;
import com.example.tobyspringtutorial.modules.repository.SqlRegistry;

import javax.annotation.PostConstruct;

public class BaseSqlService implements SqlService{ // SQL을 가져옴.
    protected SqlRegistry sqlRegistry;
    protected SqlReader sqlReader;

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    @PostConstruct // 자바 9 이상부터 Deprecated
    public void loadSql(){
        sqlReader.read(this.sqlRegistry);
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return sqlRegistry.findSql(key);
        }catch (SqlNotFoundException e){
            throw new SqlRetrievalFailureException(e);
        }
    }
}
