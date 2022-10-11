package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;
import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConcurrentHashMapSqlRegistryTest {
    private UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp(){
        sqlRegistry = new ConcurrentHashMapSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    @Test
    public void find(){
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    // 반복적으로 검증하는 부분을 따로 메서드로 분리하면 코드가 더 깔끔해진다.
    private void checkFindResult(String expected1, String expected2, String expected3){
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo(expected1);
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo(expected2);
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo(expected3);
    }

    @Test
    public void unknownKey(){
        assertThrows(SqlNotFoundException.class, () -> sqlRegistry.findSql("WTF"));
    }

    @Test
    public void updateSingle(){
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti(){
        // given
        HashMap<String, String> sqlMap = new HashMap<>();
        sqlMap.put("KEY1", "Modified1");
        sqlMap.put("KEY3", "Modified3");
        // when
        sqlRegistry.updateSql(sqlMap);
        // then
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test
    public void updateWithNotExistingKey(){
        assertThrows(SqlUpdateFailureException.class, () -> sqlRegistry.updateSql("WTF", "Modified2"));
    }
}
