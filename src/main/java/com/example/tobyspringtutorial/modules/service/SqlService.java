package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.exceptions.SqlRetrievalFailureException;

public interface SqlService { // SQL 가져옴
    String getSql(String key) throws SqlRetrievalFailureException;
}
