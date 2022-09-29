package com.example.tobyspringtutorial.modules.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

// 스프링 프록시 팩토리 빈을 이용하도록 만들어진 클래스. 타겟 정보를 가지고 있지 않는다.
public class TransactionAdvice implements MethodInterceptor {
    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable { // 타겟을 호출하는 기능을 가진 콜백 오브젝트(invocation)를 프록시로부터 받는다.
        // 덕분에 어드바이스는 특정 타겟에 의존하지 않고 재사용이 가능하다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object ret = invocation.proceed(); // 콜백을 호출해서 타겟의 메서드를 실행한다.
            // 타겟 메서드 호출 전후로 필요한 부가기능을 넣을 수 있다. 경우에 따라 타겟이 아예 호출되지 않게 하거나
            // 재시도를 위한 반복적인 호출도 가능하다.
            this.transactionManager.commit(status);
            return ret;
        }catch (RuntimeException e){ // 기존 다이나믹 프록시와 달리 스프링의 MethodInvocation을 통한 타겟 호출은 예외가 감싸지지 않는다.
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
