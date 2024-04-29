package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void unchecked_throws() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrows).isInstanceOf(MyUncheckedException.class);
    }
    /**
     * RuntimeException을 상속받은 예외는 언체크 예외
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 언체크 예외는 예외를 잡거나, 던지지 않아도 됨
     * 잡지 않는다면 -> 자동으로 밖으로 던진다
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외 잡기
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 처리, message={}",e.getMessage(), e);
            }
        }

        /**
         * 언체크 예외를 잡거나 직접 던지지 않아도
         * 자동으로 던진다
         */
        public void callThrows() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
