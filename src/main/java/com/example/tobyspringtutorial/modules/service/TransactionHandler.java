package com.example.tobyspringtutorial.modules.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// TransactionTx에 다이나믹 프록시를 적용한 클래스.
public class TransactionHandler implements InvocationHandler {
    private Object target; // 부가기능을 제공할 타겟 오브젝트. 어떤 오브젝트에도 열려있음.
    private PlatformTransactionManager transactionManager; // 트랜잭션 기능 제공에 필요한 트랜잭션 매니저
    private String pattern; // 트랜잭션을 적용할 메서드 이름 패턴

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith(pattern)) // 메서드 이름 시작부를 pattern과 비교하여 그와 같다면 실행.
            return invokeInTransaction(method, args); // 부가기능 추가 + 위임
        else{
            return method.invoke(target, args); // 단순 위임
        }
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable{
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            Object ret = method.invoke(target, args);
            transactionManager.commit(status);
            return ret;
        }catch (InvocationTargetException e){ // 리플렉션 메서드인 invoke를 이용할 때는 이 예외처리로 예외를 잡아야함. 실제 예외가 내부에 감싸져 있다.
            // https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/reflect/InvocationTargetException.html
            transactionManager.rollback(status);
            throw e.getTargetException(); // 감싸져 있는 실제 타겟의 예외를 던진다.
        }
    }
}
