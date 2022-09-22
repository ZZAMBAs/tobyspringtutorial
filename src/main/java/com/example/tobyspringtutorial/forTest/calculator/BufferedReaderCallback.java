package com.example.tobyspringtutorial.forTest.calculator;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallback { // 템플릿 / 콜백 패턴에서 콜백을 위한 인터페이스
    Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
