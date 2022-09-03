package com.example.tobyspringtutorial.modules;

// Enum: http://www.tcpschool.com/java/java_api_enum
public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;
    Level(int value) {
        this.value = value;
    }

    public int intValue() { // DB와의 타입을 맞춰주기 위해 필요한 메서드 (객체(Level) -> DB(int))
        return value;
    }


    public static Level valueOf(int value){ // DB와의 타입을 맞춰주기 위해 필요한 메서드 (DB(int) -> 객체(Level))
        // 에러는 언체크 예외여서 throws 필요 없음.
        switch (value){
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }

}
