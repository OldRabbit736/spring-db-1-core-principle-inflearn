package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 * 순수한 비즈니스 로직만 남고, 트랜잭션 관련 코드는 모두 삭제되었다.
 * 스프링이 제공하는 트랜잭션 AOP를 적용하기 위해 @Transactional 애노테이션이 추가되었다.
 * @Transactional 애노테이션은 메서드에 붙여도 되고 클래스에 붙여도 된다.
 * 클래스에 붙이면 외부에서 호출 가능한 public 메서드가 AOP 적용 대상이 된다.
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    /*
    transaction 시작,
    비즈니스 로직 실행
    Runtime Exception 발생 시 rollback (default) (설정 변경 가능)
    그 외의 경우(로직 성공, Checked Exception 발생) commit
    --> 프록시 객체로 기능 제공
     */
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money); // row의 lock 획득, 데이터 저장 (임시)
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money); // row의 lock 획득, 데이터 저장 (임시)
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
