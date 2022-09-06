package com.example.tobyspringtutorial.modules.objects;

import com.example.tobyspringtutorial.modules.DaoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DaoFactory.class)
class UserTest {
    User user;

    @BeforeEach
    void setup(){
        this.user = new User("", "", "", null, 0, 0);
    }

    @Test
    public void upgradeLevel(){ // 정상 업그레이드 확인 테스트
        // given
        Level[] levels = Level.values();
        // when
        for (Level level : levels){
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            // then
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    public void cannotUpgradeLevel(){ // 업그레이드 불가 경우 테스트
        // given
        Level[] levels = Level.values();
        // when
        for (Level level : levels){
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            // then
            assertThrows(IllegalStateException.class, () ->user.upgradeLevel()); // 다음 레벨이 null인데 강제로 업그레이드
        }
    }

}