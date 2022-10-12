package com.example.tobyspringtutorial.modules.repository;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
