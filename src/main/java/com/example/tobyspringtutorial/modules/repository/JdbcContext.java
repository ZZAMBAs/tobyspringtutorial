package com.example.tobyspringtutorial.modules.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// @RequiredArgsConstructor
public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException { // 템플릿/콜백 패턴: https://limkydev.tistory.com/85
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) {} }
            if (c != null) {try{ c.close(); } catch (SQLException e) {} }
        }
    }

    public void executeSql(final String sql) throws SQLException { // 중복부분 따로 설정. 파라미터 없는 SQL 용
        workWithStatementStrategy(
                c -> c.prepareStatement(sql)); // 람다식: https://makecodework.tistory.com/entry/Java-%EB%9E%8C%EB%8B%A4%EC%8B%9DLambda-%EC%9D%B5%ED%9E%88%EA%B8%B0
    }

    public void executeSql(final String sql, final String ...binding) throws SQLException { // 파라미터 필요한 SQL 용
        workWithStatementStrategy(
                c -> {
                    PreparedStatement ps = c.prepareStatement(sql);
                    for (int i = 0; i < binding.length; i++)
                        ps.setString(i + 1, binding[i]);
                    return ps;
                });
    }

}
