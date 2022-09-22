package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.forTest.forFactoryBean.Message;
import com.example.tobyspringtutorial.forTest.forFactoryBean.MessageFactoryBean;
import com.example.tobyspringtutorial.modules.DaoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DaoFactory.class)
public class FactoryBeanTest {
    @Autowired
    ApplicationContext ac;

    @Test
    public void getMessageFromFactoryBean(){
        // given
        Object message = ac.getBean("message"); // 팩토리 빈으로 인해 얻어지는 빈은 Message 오브젝트이다. (MessageFactoryBean이 아니다!)
        // then
        assertThat(message).isInstanceOf(Message.class); // 위 설명대로 타입은 Message.class
        assertThat(((Message)message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    public void getMessageFactoryBean(){
        // given
        Object messageFactoryBean = ac.getBean("&message"); // 빈 이름 앞에 &을 붙이면 팩토리 빈 그 자체를 가져온다.
        // then
        assertThat(messageFactoryBean).isInstanceOf(MessageFactoryBean.class);
    }
}
