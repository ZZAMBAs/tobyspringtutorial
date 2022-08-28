package com.example.tobyspringtutorial.modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException{
        // D 사 만의 DB 커넥션 따오는 코드 예시
        return DriverManager.getConnection("jdbc:/mysql://localhost:3306/test", "root", "1234");
    }
}
