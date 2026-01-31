package com.naoki.investmentledger.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // アプリ全体の「相談役」として登録
public class GlobalExceptionHandler {

    // 「バリデーションエラー(MethodArgumentNotValidException)」が起きたら、このメソッドが対応する
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // エラー内容を1つずつ取り出して、Mapに詰める
        // 例: "fundName": "銘柄名は必須です"
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 400 Bad Request と一緒に、エラー一覧を返す
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
