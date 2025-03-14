package com.farmorai.backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class ResponseApi<T> {
    private final int status;
    private final String message;
    private final T data;


    // ✅ 성공 응답 (데이터 포함 + 메시지 지정 + 상태 코드(200))
    public static <T> ResponseApi<T> success(String message, T data) {
        return new ResponseApi<>(HttpStatus.OK.value(), message, data);
    }

    // ✅ 성공 응답 (데이터 포함 + 메시지 지정 + 상태 코드 지정)
    public static <T> ResponseApi<T> success(String message, T data, HttpStatus status) {
        return new ResponseApi<>(status.value(), message, data);
    }

    // ✅ 실패 응답 (에러 메시지 + 상태 코드)
    public static <T> ResponseApi<T> error(String message, HttpStatus status) {
        return new ResponseApi<>(status.value(), message, null);
    }
}