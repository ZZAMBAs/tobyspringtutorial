package com.example.tobyspringtutorial.learningTests.forDynamicProxy;

// 타겟 오브젝트
public class HelloTarget implements Hello{
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }

    @Override
    public String sayThankYou(String name) {
        return "Thank You, " + name;
    }
}
