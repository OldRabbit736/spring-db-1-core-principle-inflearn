package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랜잭션 시작
            bizLogic(con, fromId, toId, money); // 비즈니스 로직 수행
            con.commit(); // 커밋
        } catch (Exception e) {
            con.rollback(); // 실패 시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        // 모두 같은 Connection(--> 같은 세션 --> 같은 트랜잭션)으로 SQL 실행
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);
        memberRepository.update(con, fromId, fromMember.getMoney() - money); // row의 lock 획득, 데이터 저장 (임시)
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money); // row의 lock 획득, 데이터 저장 (임시)
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                // 만약 DataSource가 Connection Pool일 경우 커넥션 close 하는 경우 해당 커넥션이 풀로 돌아가게 된다.
                // 즉 재사용되므로, 원래 상태로 되돌려 주어야 한다. (autocommit의 디폴트 값은 보통 true 이다.)
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
