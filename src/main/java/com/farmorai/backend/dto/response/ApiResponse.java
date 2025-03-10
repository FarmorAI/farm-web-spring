package com.farmorai.backend.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // ✅ 성공 응답 (데이터 포함 + 메시지 지정 + 상태 코드(200))
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, data);
    }

    // ✅ 성공 응답 (데이터 포함 + 메시지 지정 + 상태 코드 지정)
    public static <T> ApiResponse<T> success(String message, T data,HttpStatus status) {
        return new ApiResponse<>(status.value(), message, data);
    }

    // ✅ 실패 응답 (에러 메시지 + 상태 코드)
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(status.value(), message, null);
    }
}