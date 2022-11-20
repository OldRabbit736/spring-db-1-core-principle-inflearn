package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        /**
         * PreparedStatement는 Statement의 자식 타입으로 파라미터 바인딩 기능이 추가되었다.
         * SQL injection 공격을 예방하려면 PreparedStatement를 통한 파라미터 바인딩 방식을 사용해야 한다.
         */
        PreparedStatement pstmt = null;

        try {
            con = getConnection();  // 데이터베이스와 연결
            pstmt = con.prepareStatement(sql);  // 데이터베이스에 전달할 SQL 준비
            pstmt.setString(1, member.getMemberId());   // SQL에 값 삽입(바인딩)
            pstmt.setInt(2, member.getMoney()); // SQL에 값 삽입(바인딩)
            int count = pstmt.executeUpdate();// SQL을 커넥션을 통해 데이터베이스에 전달, 영향받은 DB rows 수 반환
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // 실제 connection 되고 있는 상황이므로 리소스 낭비를 막기위해 닫아준다.
            // 닫지 않으면 리소스 누수에 의해 커넥션 부족 장애로 이어질 수 있다.
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        // 리소스 정리는 역순으로
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // 문제가 생겨도 딱히 할 수 있는 것은 없다. 그냥 로그만 찍는다.
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // 문제가 생겨도 딱히 할 수 있는 것은 없다. 그냥 로그만 찍는다.
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // 문제가 생겨도 딱히 할 수 있는 것은 없다. 그냥 로그만 찍는다.
                log.info("error", e);
            }
        }
    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
