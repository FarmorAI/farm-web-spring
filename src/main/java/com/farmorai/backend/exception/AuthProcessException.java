package com.farmorai.backend.exception;

/**
 * 사용자 정의 런타임 예외 클래스
 * "RuntimeException"을 상속 받았기 때문에 명시적인 예외 처리가 강제되지 않음
 * super(message, cause); 통해 부모 클래스인 *RuntimeException"의 생성자 호출
 */
public class AuthProcessException extends RuntimeException {
    public AuthProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
