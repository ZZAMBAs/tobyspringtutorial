package com.example.tobyspringtutorial.modules;

import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;

abstract public class UserDao {
    // private ConnectionMaker connectionMaker;
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    /*public UserDao(ConnectionMaker connectionMaker){
        this.connectionMaker = connectionMaker;
    }*/

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;

        //이부분부터는 JdbcContext 수동 DI (스프링 DI 아님)를 위한 코드. 수동 DI는 스프링 DI / 빈이 아니므로 싱글톤이 될 수 없다.
        jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource);
        // 이부분까지
    }

    /*public void setJdbcContext(JdbcContext jdbcContext){ // 인터페이스가 아닌 구체 클래스를 DI 받고있다.
        this.jdbcContext = jdbcContext;
    }*/ // 스프링 DI를 이용할 때를 위한 코드

    public void add(User user) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("insert into users values(?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }catch (SQLException e){ throw e; }
        finally {
            if (ps != null) try{ ps.close(); } catch (SQLException e) {}
            if (c != null) try{ c.close(); } catch (SQLException e) {}
        }
    }

    public void addByS(User user) throws SQLException { // 전략 패턴을 이용한 add
        StatementStrategy st = new AddStatement(user);
        jdbcContext.workWithStatementStrategy(st);
    }

    public void addByS_localClassUsed(final User user) throws SQLException { // 로컬(내부) 클래스를 이용한 전략 패턴.
        // 로컬 클래스는 해당 메서드 내에서만 사용되는 클래스다. (로컬 변수처럼!)
        class AddStatement implements StatementStrategy{
            // 내부 클래스에서 외부 변수를 쓸 때는 외부 변수는 final로 변경 금지를 하도록 해야 한다.

            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users values (?, ?, ?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        }

        StatementStrategy st = new AddStatement();
        /* StatementStrategy st = new StatementStrategy(){ // 이것처럼 익명 내부 클래스로 선언도 가능하다.
              public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                  PreparedStatement ps = c.preparedStatement("insert into users values(?, ?, ?)");
                  ps.setString(1, user.getId());
                  ps.setString(2, user.getUserName());
                  ps.setString(3, user.getPassword());
              }

              return ps;
           }
        */
        jdbcContext.workWithStatementStrategy(st);
    }

    public void addByS_TC(final User user) throws SQLException {
        jdbcContext.executeSql("insert into users values(?, ?, ?)", user.getId(), user.getUserName(), user.getPassword());
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if(!rs.next())
            throw new EmptyResultDataAccessException(1);
        User user = new User(rs.getString("id"), rs.getString("username"), rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }

    public void deleteAllbyT() throws SQLException { // 템플릿 메서드를 이용한 deleteAll
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = makeDeleteStatement(c); // 템플릿 메서드 패턴
            ps.executeUpdate();
        }catch (SQLException e){
            throw e;
        }finally {
            if (ps != null){
                try {
                    ps.close();
                }catch (SQLException e){}
            }

            if (c != null){
                try {
                    c.close();
                }catch (SQLException e) {}
            }
        }

    }

    abstract protected PreparedStatement makeDeleteStatement(Connection c) throws SQLException;

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();

            rs.next();
            return rs.getInt(1);
        }catch (SQLException e){
            throw e;
        }finally {
            if (rs != null){
                try{
                    rs.close();
                }catch (SQLException e){}
            }
            if (ps != null){
                try{
                    ps.close();
                }catch (SQLException e){}
            }
            if (c != null){
                try {
                    c.close();
                }catch (SQLException e){}
            }
        }

    }

    public void deleteAllbyS() throws SQLException { // 전략 패턴을 이용한 deleteAll
        StatementStrategy st = new DeleteAllStatement(); // 선택한 전략의 오브젝트 생성
        jdbcContext.workWithStatementStrategy(st); // 컨텍스트 호출, 전략 오브젝트 전달
    }

    public void deleteAllbyS_TC() throws SQLException { // 템플릿 콜백 패턴을 이용한 deleteAll
        jdbcContext.executeSql("delete from users"); // 컨텍스트 호출
    }

    /*public void jdbcContextWithStatementStrategy(StatementStrategy strategy) throws SQLException{ // 전략 패턴
        // Client가 전략을 선택한다.
        // 공통 부분은 그대로, 바뀌는 부분은 makePreparedStatement 메서드로 구성되어 있으며, 이 메서드도 전략에 따라 변화한다.
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = strategy.makePreparedStatement(c);
            ps.executeUpdate();
        }catch (SQLException e){
            throw e;
        }finally {
            if(ps != null){ try { ps.close(); } catch (SQLException e){} }
            if(c != null){ try { c.close(); } catch (SQLException e){} }
            }
    }*/ // JdbcContext 클래스로 따로 뺌. 이 코드는 여러군데에서 재사용되기 좋기 때문.

}
