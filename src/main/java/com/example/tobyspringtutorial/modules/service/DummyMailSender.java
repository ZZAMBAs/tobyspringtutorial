package com.example.tobyspringtutorial.modules.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

// 테스트용 메일 발송 오브젝트(테스트 대역 - 테스트 스텁)
public class DummyMailSender implements MailSender {
    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        System.out.println("단일 파라미터 send 실행됨.");
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        System.out.println("다중 파라미터 send 실행됨.");
    }
}
