package com.example.tobyspringtutorial.learningTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

// 스프링에는 내장형 DB 지원 기능을 가지고 있다.
public class EmbeddedDbTest {
    private EmbeddedDatabase db;
    private JdbcTemplate template;

    @BeforeEach
    public void setUp(){
        db = new EmbeddedDatabaseBuilder().setType(H2).addScript("classpath:schema.sql") // 테이블 생성 스크립트
                .addScript("classpath:data.sql").build(); // 테이블 초기화 스크립트
        template = new JdbcTemplate(db); // EmbeddedDatabase는 DataSource의 서브 인터페이스이다.
    }

    @AfterEach
    public void tearDown(){ // 내장형 DB는 따로 저장하지 않는 한 애플리케이션과 함께 매번 새롭게 DB가 만들어지고 제거되는 생명주기를 가진다.
        db.shutdown(); // 종료
    }

    @Test
    public void initData(){ // 초기화 스크립트(data.sql)를 통해 등록한 데이터 검증 테스트
        assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", Integer.class)).isEqualTo(2);

        List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY KEY_");
        assertThat(list.get(0).get("KEY_")).isEqualTo("KEY1");
        assertThat(list.get(0).get("SQL_")).isEqualTo("SQL1");
        assertThat(list.get(1).get("KEY_")).isEqualTo("KEY2");
        assertThat(list.get(1).get("SQL_")).isEqualTo("SQL2");
    }

    @Test
    public void insert(){ // 새 데이터 추가 테스트
        // when
        template.update("INSERT INTO SQLMAP VALUES(?, ?)", "KEY3", "SQL3");
        // then
        assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", Integer.class)).isEqualTo(3);
    }
}
