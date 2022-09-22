package com.example.tobyspringtutorial.modules.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> { // Object로 설정해 범용적으로 사용할 수 있게끔 했다.
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;
    Class<?> serviceInterface; // 다이나믹 프록시 생성 시 구현할 인터페이스. UserService 외 인터페이스를 가진 타겟에도 적용 가능.
    // 제네릭에서 ?는 와일드카드를 뜻한다. http://www.tcpschool.com/java/java_generic_various

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    // 팩토리 빈 인터페이스 구현 메서드
    @Override
    public Object getObject() {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setPattern(pattern);
        txHandler.setTransactionManager(transactionManager);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ serviceInterface }, txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false; // 싱글톤 빈이 아니란 뜻이 아니고 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 의미.
    }
}
