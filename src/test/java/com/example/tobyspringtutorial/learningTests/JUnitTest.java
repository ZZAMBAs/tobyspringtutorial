package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.configForTest.JUnitConfig;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// JUnit이 테스트 메서드를 실행할 때마다 새로운 테스트 오브젝트를 생성하는지 알아보는 테스트
// + 스프링의 @autowired 된 ac는 항상 동일한지 알아보는 테스트(test1 ~ 3)
@SpringBootTest(classes = JUnitConfig.class) // 테스트용 설정 정보 주입
public class JUnitTest {
    @Autowired ApplicationContext ac;
    private static Set<JUnitTest> previousTestObjs = new HashSet<>();
    private static ApplicationContext acObj = null;

    @Test
    void test1(){
        assertThat(previousTestObjs).doesNotContain(this);
        previousTestObjs.add(this);

        assertThat(acObj == null || acObj == ac).isTrue();
        acObj = ac;
    }

    @Test
    void test2(){
        assertThat(previousTestObjs).doesNotContain(this);
        previousTestObjs.add(this);

        assertEquals(true, acObj == null || acObj == ac);
        acObj = ac;
    }

    @Test
    void test3(){
        assertThat(previousTestObjs).doesNotContain(this);
        previousTestObjs.add(this);

        assertTrue(acObj == null || acObj == ac);
        acObj = ac;
    }

    @Test
    void test4(){ // 해당 빈이 있는지 알아보는 테스트
        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("userDao", UserDao.class));
    }

}
