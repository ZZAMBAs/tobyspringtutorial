package com.example.tobyspringtutorial.modules.repository;

import com.example.tobyspringtutorial.modules.objects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStatement implements StatementStrategy{
    private User user;

    public AddStatement(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into users values (?, ?, ?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getUserName());
        ps.setString(3, user.getPassword());

        return ps;
    }
}