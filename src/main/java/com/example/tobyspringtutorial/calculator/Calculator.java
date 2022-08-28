package com.example.tobyspringtutorial.calculator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Calculator { // 템플릿 콜백 패턴 연습을 위한 클래스

    /*public Integer calcSum(String path) throws IOException { // 자바 파일 읽기: https://hianna.tistory.com/587
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            Integer sum = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null)
                sum += Integer.parseInt(line);

            return sum;
        }catch (IOException e){ throw e; }
        finally {
            if (bufferedReader != null)
                try{
                    bufferedReader.close(); // close 필수
                } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }*/ // 템플릿 콜백 패턴 적용 안한 경우 코드
    
    public Integer calcSum(final String filepath) throws IOException{
        /*BufferedReaderCallback callback = br -> {
            Integer sum = 0;
            String line;
            while ((line = br.readLine()) != null)
                sum += Integer.parseInt(line);

            return sum;
        };
        
        return fileReadTemplate(filepath, callback);*/

        return lineReadTemplate(filepath, (line, value) -> value + Integer.parseInt(line), 0);
    }

    public Integer calcMul(final String filePath) throws IOException {
        /*return fileReadTemplate(filePath, br -> {
            Integer val = 1;
            String line;

            while ((line = br.readLine()) != null)
                val *= Integer.parseInt(line);
            return val;
        }); // calcMul 과 calcSum 은 구조가 유사하다. 유사한 코드는 템플릿 / 콜백 패턴을 부른다. 아래 lineReadTemplate 참조*/

        return lineReadTemplate(filePath, (line, value) -> value * Integer.parseInt(line), 1); // 더 간단해진 코드
    }

    public String concatenate(String filepath) throws IOException {
        return lineReadTemplate(filepath, (line, value) -> value + line, "");
    }

    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filepath));
            Integer ret = callback.doSomethingWithReader(bufferedReader);

            return ret;
        }catch (IOException e){ throw e; }
        finally {
            if (bufferedReader != null)
                try{
                    bufferedReader.close(); // close 필수
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
        }

    }

    public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException{
        // 위 fileReadTemplate에서 중복되는 부분을 더 템플릿화
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line;
            while ((line = bufferedReader.readLine()) != null)
                res = callback.doSomethingWithLine(line, res);
            return res;
        }catch (IOException e){ throw e; }
        finally {
            if (bufferedReader != null)
                try{
                    bufferedReader.close(); // close 필수
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
        }

    }
}
