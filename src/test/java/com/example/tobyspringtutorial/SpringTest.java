package com.example.tobyspringtutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class SpringTest {

    @Autowired
    private ApplicationContext ac;
}
