package com.example.tobyspringtutorial.learningTests.forDynamicProxy;

// 데코레이터 패턴이 적용된 프록시. 리턴하는 문자열을 모두 대문자로 바꿈.
// 프록시 적용의 일반적인 문제점을 모두 가지고 있다. 1. 중복 코드 존재(리턴값 대문자로 바꾸기) 2. 인터페이스의 모든 메서드를 구현하여 위임해야 함.
public class HelloUppercase implements Hello{
    private final Hello helloTarget;

    public HelloUppercase(Hello helloTarget) {
        this.helloTarget = helloTarget;
    }

    @Override
    public String sayHello(String name) {
        return helloTarget.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return helloTarget.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return helloTarget.sayThankYou(name).toUpperCase();
    }
}
