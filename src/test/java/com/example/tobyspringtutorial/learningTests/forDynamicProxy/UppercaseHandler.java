package com.example.tobyspringtutorial.learningTests.forDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// 기존 프록시 적용에 대한 문제점을 없애기 위해 다이나믹 프록시를 적용한다.
// InvocationHandler는 타겟에 위임, 부가기능 제공 코드를 작성하게끔 한다.
// 다이나믹 프록시: https://url.kr/dt8aom
public class UppercaseHandler implements InvocationHandler {
    private final Object target; // 어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 한다.

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        if (ret instanceof String){ // 타입이 String 일 때만. method 가 가진 메서드를 이용해 더 제한할 수도 있음.
            return ((String) ret).toUpperCase(); // 부가기능 제공. 리턴 값은 다이나믹 프록시가 받아 최종적으로 클라이언트에게 전달된다.
        }
        else {
            return ret;
        }

    }
}
