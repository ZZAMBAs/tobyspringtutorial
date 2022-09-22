package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.learningTests.forDynamicProxy.Hello;
import com.example.tobyspringtutorial.learningTests.forDynamicProxy.HelloTarget;
import com.example.tobyspringtutorial.learningTests.forDynamicProxy.HelloUppercase;
import com.example.tobyspringtutorial.learningTests.forDynamicProxy.UppercaseHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

// https://kdg-is.tistory.com/entry/JAVA-%EB%A6%AC%ED%94%8C%EB%A0%89%EC%85%98-Reflection%EC%9D%B4%EB%9E%80
// 다이나믹 프록시를 설명하기 전에 리플렉션을 알아둬야 함. 그것이 전제기 때문.
public class ProxyTest {

    // Reflection 테스트
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

    @Test
    public void simpleDynamicProxy(){
        // given
        Hello proxiedHello = (Hello) Proxy.newProxyInstance( // 다이나믹 프록시 오브젝트를 생성.
                // JVM 클래스 로더에 대해: https://steady-coding.tistory.com/593
                getClass().getClassLoader(), // 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{ Hello.class }, // 구현할 인터페이스. 여러개 구현도 가능해서 배열로 담는다.
                new UppercaseHandler(new HelloTarget())); // 부가기능과 위임 코드를 담은 InvocationHandler 구현 오브젝트
        // then
        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO, TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI, TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU, TOBY");
    }

    @Test
    public void proxyFactoryBean(){
        // given
        ProxyFactoryBean pfBean = new ProxyFactoryBean(); // 스프링에서 지원하는 프록시 팩토리 빈
        pfBean.setTarget(new HelloTarget()); // 타겟 설정
        pfBean.addAdvice(new UppercaseAdvice()); // 부가기능 추가 메서드
        // when
        Hello proxiedHello = (Hello) pfBean.getObject();
        // then
        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO, TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI, TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU, TOBY");
    }

    static class UppercaseAdvice implements MethodInterceptor{ // InvocationHandler 역할을 MethodInterceptor가 한다.
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            // Reflection의 Method와 달리 메서드 실행 시 타겟 오브젝트를 전달할 필요가 없다.
            // MethodInvocation은 메서드 정보와 함께 타겟 오브젝트를 이미 알고 있기 때문.
            return ret.toUpperCase(); // 부가기능 적용.
        }
    }
}
