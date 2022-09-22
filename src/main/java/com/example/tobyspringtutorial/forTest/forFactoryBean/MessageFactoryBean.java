package com.example.tobyspringtutorial.forTest.forFactoryBean;

import org.springframework.beans.factory.FactoryBean;

// 팩토리 빈은 스프링을 대신해 오브젝트 생성로직을 담당하도록 만들어진 특별한 빈을 말한다.
// 스프링은 팩토리 빈을 구현한 클래스를 빈으로 등록하면 해당 클래스의 getObject()로 오브젝트를 가져오고 이를 빈 오브젝트로 사용한다.
public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    public void setText(String text) { // 오브젝트를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서 대신 DI 받을 수 있도록 함.
        this.text = text;
    }

    @Override
    public boolean isSingleton() { // getObject()가 돌려주는 오브젝트가 싱글톤인지 알려줌.
        return false; // 팩토리 빈은 매번 요청때마다 새로운 오브젝트를 만드므로 false다.
    }

    @Override
    public Message getObject() { // 실제 빈으로 사용될 오브젝트를 직접 생성.
        return Message.newMessage(this.text);
    }

    @Override
    public Class<? extends Message> getObjectType() {
        return Message.class;
    }
}
