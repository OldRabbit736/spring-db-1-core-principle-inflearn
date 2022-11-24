package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeSQLException.class);
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
    }
}
