package com.example.grouple.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String status;
    private final String message;
    private final T data;
    private final ErrorDetail error;

    private ApiResponse(String status, String message,T data, ErrorDetail error) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", null, data, null);
    }
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>("success", message, null, null);
    }
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>("error", null, null, new ErrorDetail(code, message));
    }

    public record ErrorDetail(int code, String message) {
    }
}