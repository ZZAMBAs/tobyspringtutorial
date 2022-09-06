package com.example.tobyspringtutorial.modules.objects;

// Enum: http://www.tcpschool.com/java/java_api_enum
public enum Level {
    GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER); // 이제 레벨들은 다음 레벨 정보도 담는다.

    private final int value;
    private final Level nextLevel;

    Level(int value, Level nextLevel) {
        this.value = value;
        this.nextLevel =nextLevel;
    }

    public int intValue() { // DB와의 타입을 맞춰주기 위해 필요한 메서드 (객체(Level) -> DB(int))
        return this.value;
    }

    public Level nextLevel(){
        return this.nextLevel;
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
