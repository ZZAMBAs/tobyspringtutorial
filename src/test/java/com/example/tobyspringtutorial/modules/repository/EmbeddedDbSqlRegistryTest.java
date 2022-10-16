package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;


public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    private EmbeddedDatabase db;

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

    // 트랜잭션에 의한 롤백 테스트.
    @Test
    public void transactionUpdate(){
        super.checkFindResult("SQL1", "SQL2", "SQL3");

        Map<String, String> sqlMap = new HashMap<>();
        sqlMap.put("KEY1", "Modified1");
        sqlMap.put("InvalidKey", "yeah~");

        try {
            sqlRegistry.updateSql(sqlMap); // 한번에 업데이트
            fail("예외 발생이 안되었습니다."); // 실패. 여기 도달하면 안됨.
        }catch (SqlUpdateFailureException e){}

        checkFindResult("SQL1", "SQL2", "SQL3"); // 모두 롤백된다.
    }
}
