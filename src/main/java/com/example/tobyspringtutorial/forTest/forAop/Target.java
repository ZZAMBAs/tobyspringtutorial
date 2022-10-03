package com.example.tobyspringtutorial.forTest.forAop;

// 포인트컷 테스트 용
public class Target implements TargetInterface{
    @Override
    public void hello() {

    }

    @Override
    public void hello(String a) {

    }

    @Override
    public int minus(int a, int b) {
        return 0;
    }

    @Override
    public int plus(int a, int b) {
        return 0;
    }

    public void method() {}
}
