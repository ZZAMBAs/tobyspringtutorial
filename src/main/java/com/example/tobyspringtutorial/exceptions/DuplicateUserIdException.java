package com.example.tobyspringtutorial.exceptions;

public class DuplicateUserIdException extends RuntimeException{ // 복구 가능이든 복구 불가능이든 언체크 예외로 하여 던진다.
    // 예외: http://plus4070.github.io/nhn%20entertainment%20devdays/2017/01/22/Exception/

    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}
