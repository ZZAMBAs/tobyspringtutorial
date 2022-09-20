package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.learningTests.forDynamicProxy.Hello;
import com.example.tobyspringtutorial.learningTests.forDynamicProxy.HelloTarget;
import com.example.tobyspringtutorial.learningTests.forDynamicProxy.HelloUppercase;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

// https://kdg-is.tistory.com/entry/JAVA-%EB%A6%AC%ED%94%8C%EB%A0%89%EC%85%98-Reflection%EC%9D%B4%EB%9E%80
// 다이나믹 프록시를 설명하기 전에 리플렉션을 알아둬야 함. 그것이 전제기 때문.
public class ReflectionTest {
    @Test
    public void invokeMethod() throws Exception{
        // given
        String name = "string";
        Method lengthMethod = String.class.getMethod("length");
        Method charAtMethod = name.getClass().getMethod("charAt", int.class);
        // 파라미터 필요시, 파라미터 수 만큼 순서대로 파라미터 타입 클래스를 나열한다. charAt은 int형 파라미터가 1개 필요하므로 위처럼 표기.

        // then
        assertThat(lengthMethod.invoke(name)).isEqualTo(6); // 메서드.invoke(오브젝트, ...파라미터): 해당 오브젝트에서 메서드 실행.
        assertThat(charAtMethod.invoke(name, 0)).isEqualTo('s');
    }

    @Test
    public void simpleProxy(){
        // given
        Hello hello = new HelloTarget(); // 타겟은 인터페이스로 가져오는 습관을 들인다. 프록시 적용 안되어 있음.
        Hello proxiedHello = new HelloUppercase(hello); // 프록시가 적용됨.

        // then
        assertThat(hello.sayHello("Toby")).isEqualTo("Hello, Toby");
        assertThat(hello.sayHi("Toby")).isEqualTo("Hi, Toby");
        assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You, Toby");

        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO, TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI, TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU, TOBY");
    }
}
