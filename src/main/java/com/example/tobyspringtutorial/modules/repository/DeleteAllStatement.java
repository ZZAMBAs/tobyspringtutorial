package com.example.tobyspringtutorial.modules.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy{
    @Override
    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
        // 전략 패턴
        // https://refactoring.guru/design-patterns/strategy
        // https://victorydntmd.tistory.com/292
        return c.prepareStatement("delete from users");
    }
}
