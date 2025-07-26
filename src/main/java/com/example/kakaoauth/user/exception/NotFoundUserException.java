package com.example.kakaoauth.user.exception;

import com.example.kakaoauth.global.ErrorCode;
import com.example.kakaoauth.global.exception.BusinessException;

public class NotFoundUserException extends BusinessException {
    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
