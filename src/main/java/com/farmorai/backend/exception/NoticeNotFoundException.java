package com.farmorai.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoticeNotFoundException extends RuntimeException {
    public NoticeNotFoundException(String message) {
        super(message);
    }

    public NoticeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoticeNotFoundException(Throwable cause) {
        super(cause);
    }

    public NoticeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NoticeNotFoundException() {
    }
}
