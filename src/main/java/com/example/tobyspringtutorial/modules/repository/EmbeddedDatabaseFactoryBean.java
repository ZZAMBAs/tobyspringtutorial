package com.example.tobyspringtutorial.modules.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

// 초기화 코드가 필요해서 팩토리 빈으로 생성하였다.
public class EmbeddedDatabaseFactoryBean implements FactoryBean<EmbeddedDatabase> {
    EmbeddedDatabase db;
    String[] initSqlResourcePath;

    public void setInitSqlResourcePath(String ...initSqlResourcePath) {
        this.initSqlResourcePath = initSqlResourcePath;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    @Override
    public EmbeddedDatabase getObject() {
        EmbeddedDatabaseBuilder databaseBuilder = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2);
        if (initSqlResourcePath.length > 0) {
            for (String init : initSqlResourcePath)
                databaseBuilder.addScript(init);
        }
        db = databaseBuilder.build();
        return db;
    }

    @Override
    public Class<?> getObjectType() {
        return EmbeddedDatabase.class;
    }

    private void shutdown() {
        db.shutdown();
    }
}
