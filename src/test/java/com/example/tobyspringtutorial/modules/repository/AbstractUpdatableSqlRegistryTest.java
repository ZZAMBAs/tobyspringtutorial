package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.exceptions.SqlNotFoundException;
import com.example.tobyspringtutorial.exceptions.SqlUpdateFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// ConcurrentHashMapSqlRegistry의 단위 테스트와 EmbeddedDbSqlRegistry의 단위 테스트는 매우 유사하다.
// 따라서 공통부를 추상 클래스로 하고, 각각 구체적인 부분을 구체 테스트 클래스로 생성해 단위 테스트를 완성한다.
public abstract class AbstractUpdatableSqlRegistryTest {
    protected UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp(){
        sqlRegistry = createUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry(); // 이 부분만 달라지기에 이렇게 추상 메서드화한다.

    @Test
    public void find(){
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    // 반복적으로 검증하는 부분을 따로 메서드로 분리하면 코드가 더 깔끔해진다.
    protected void checkFindResult(String expected1, String expected2, String expected3){
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
