package com.example.tobyspringtutorial.modules.repository;

import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    EmbeddedDatabase db;

    @AfterEach
    public void tearDown(){
        db.shutdown();
    }

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql").build();

        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(db);
        return sqlRegistry;
    }
}
