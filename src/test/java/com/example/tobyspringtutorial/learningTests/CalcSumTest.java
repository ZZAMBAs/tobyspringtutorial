package com.example.tobyspringtutorial.learningTests;

import com.example.tobyspringtutorial.calculator.Calculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 아래 후술
public class CalcSumTest {
    Calculator calculator = null;
    String filePath = null;

    @BeforeAll
    public void before(){
        // 기본적으로 테스트 오브젝트는 메서드마다 라이프사이클이 생성/소멸되기에 @before이 붙은 메서드는 static으로 선언해야 했다.
        // 위처럼 라이프사이클을 클래스 단위로 설정하면 그럴 필요가 없어진다.
        // https://www.baeldung.com/java-beforeall-afterall-non-static
        this.calculator = new Calculator();
        this.filePath = getClass().getClassLoader().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException{
        assertThat(calculator.calcSum(filePath)).isEqualTo(10);
    }

    @Test
    public void multipleOfNumbers() throws IOException{
        assertThat(calculator.calcMul(filePath)).isEqualTo(24);
    }

    @Test
    void concatenateString() throws IOException{
        assertThat(calculator.concatenate(filePath)).isEqualTo("1234");
    }
}
