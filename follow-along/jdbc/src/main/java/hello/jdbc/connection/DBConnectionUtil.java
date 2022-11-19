package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            // DriverManager가 라이브러리에서 적절한 Driver를 찾고 이용해 Connection 객체 얻음
            // 즉, DriverManager가 JDBC Driver 구현체를 찾고 실행시킨다.
            // DriverManager는 라이브러리에서 Driver 구현체들의 목록을 가지고 있다.
            // getConnection 메소드를 호출 시, argument 정보를 각 Driver에게 전달해주고
            // Driver는 해당 정보(jdbc:postgresql에서 postgresql 부분을 인식)를 보고 자신이 처리할 수 있는지 DriverManager에게 알려준다.
            // Driver가 자신이 처리할 수 있다고 알려줄 경우, DriverManager는 해당 Driver를 통해 Connection 객체를 얻는다. (실제 데이터베이스에 연결하여 Connection 습득)
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
