package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.repository.HashMapRegistry;
import com.example.tobyspringtutorial.modules.repository.TextSqlReader;

public class DefaultSqlService extends BaseSqlService{ // 디폴트 의존관계 빈. 거의 기본적으로 아래 의존관계 빈만을 사용할 때 만들면 좋다.
    public DefaultSqlService() {
        setSqlRegistry(new HashMapRegistry());
        TextSqlReader textSqlReader = new TextSqlReader();
        textSqlReader.setFilePath(getClass().getClassLoader().getResource("sql.txt").getPath());
        setSqlReader(textSqlReader);
    }
}
