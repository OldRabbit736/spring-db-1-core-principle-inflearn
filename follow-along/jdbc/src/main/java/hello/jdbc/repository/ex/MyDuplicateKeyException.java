package hello.jdbc.repository.ex;

/**
 * MyDbException 상속받아서 의미있는 계층을 형성한다. 즉 "데이터베이스 관련 예외"라는 계층을 만들 수 있다.
 * DB 기술에 간섭받지 않은 순수한 자바 코드이다.
 */
public class MyDuplicateKeyException extends MyDbException {
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
