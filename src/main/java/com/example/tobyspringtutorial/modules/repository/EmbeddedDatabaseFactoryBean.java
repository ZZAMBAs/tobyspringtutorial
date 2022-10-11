package com.example.tobyspringtutorial.modules.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

// 초기화 코드가 필요해서 팩토리 빈으로 생성하였다.
public class EmbeddedDatabaseFactoryBean implements FactoryBean<EmbeddedDatabase> {

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    @Override
    public EmbeddedDatabase getObject() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return EmbeddedDatabase.class;
    }
}
