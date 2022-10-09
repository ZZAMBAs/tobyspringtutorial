package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.exceptions.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
