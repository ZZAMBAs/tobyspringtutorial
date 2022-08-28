package com.example.tobyspringtutorial.calculator;

public interface LineCallback<T> { // 제네릭: https://st-lab.tistory.com/153
    T doSomethingWithLine(String line, T value);
}
