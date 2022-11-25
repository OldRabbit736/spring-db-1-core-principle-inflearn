package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();

        /**
         * 더 이상 명시적으로 예외를 던지지 않아도 된다.
         */
        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        /**
         * 더 이상 명시적으로 예외를 던지지 않아도 된다.
         */
        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    /**
     * 런타임 예외는 그대로 던진다.
     * 런타임 예외이기에 컨트롤러, 서비스에 부담을 주지 않는다.
     */
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    /**
     * 체크 예외의 경우 잡아서 런타임 예외로 변경 후 던진다.
     * 컨트롤러, 서비스에 부담을 주지 않는다.
     * 런타임 예외에 원래 예외에 대한 정보를 온전히 유지한다.
     */
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
                //기존 exception을 포함하지 않으면 스텍 트레이스에 "caused by" 정보가 없어진다.
                //그렇다면 진짜 근본 원인이 어디에서 발생했는지 추적이 불가능하다.
                //만약 실제 DB에서 발생한 예외라고 한다면, DB에서 어떤 문제가 발생해서 예외가 발생했는지 추적할 수 있는 단서가 없어지는 것이다.
                //throw new RuntimeException();
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }

        public RuntimeSQLException() {
        }
    }
}
