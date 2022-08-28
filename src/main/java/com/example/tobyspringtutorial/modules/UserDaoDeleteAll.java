package com.example.tobyspringtutorial.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoDeleteAll extends UserDao{
    @Override
    protected PreparedStatement makeDeleteStatement(Connection c) throws SQLException {
        // 템플릿 메서드 패턴.
        // https://refactoring.guru/design-patterns/template-method
        // https://yaboong.github.io/design-pattern/2018/09/27/template-method-pattern/
        return c.prepareStatement("delete from users");
    }
}
