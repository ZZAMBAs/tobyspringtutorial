package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.repository.DaoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DaoFactory.class)
class UserServiceTest {
    @Autowired
    private UserService userService;


}