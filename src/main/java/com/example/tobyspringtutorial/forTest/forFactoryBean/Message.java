package com.example.tobyspringtutorial.forTest.forFactoryBean;

// 외부에서 생성자를 통해
public class Message {
    String text;

    private Message(String test){
        this.text = test;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String test){
        return new Message(test);
    }
}
