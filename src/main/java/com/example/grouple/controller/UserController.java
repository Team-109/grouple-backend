package com.example.grouple.controller;

import com.example.grouple.dto.UserRequest;
import com.example.grouple.dto.UserResponse;
import com.example.grouple.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest request) {
        try {
            // 필수 입력값 확인
            if (request.getId() == null || request.getId().isEmpty() ||
                    request.getUsername() == null || request.getUsername().isEmpty() ||
                    request.getPassword() == null || request.getPassword().isEmpty() ||
                    request.getPasswordConfirm() == null || request.getPasswordConfirm().isEmpty() ||
                    request.getEmail() == null || request.getEmail().isEmpty() ||
                    request.getPhone() == null || request.getPhone().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(400, "필수 입력값이 누락되었습니다."));
            }

            // 비밀번호 확인
            if (!request.getPassword().equals(request.getPasswordConfirm())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(400, "비밀번호가 일치하지 않습니다."));
            }

            // ID 중복 체크
            if (userService.existsById(request.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse(409, "이미 사용 중인 ID입니다."));
            }

            // 회원가입 처리
            UserResponse newUser = userService.registerUser(request);

            // 성공 응답
            return ResponseEntity.ok(new SuccessResponse(newUser));

        } catch (Exception e) {
            // 서버 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "서버 내부 오류가 발생했습니다."));
        }
    }

    // -------------------------------
    // 성공 응답 DTO
    static class SuccessResponse {
        private String status = "success";
        private UserResponse data;

        public SuccessResponse(UserResponse data) {
            this.data = data;
        }

        public String getStatus() { return status; }
        public UserResponse getData() { return data; }
    }

    // 오류 응답 DTO
    static class ErrorResponse {
        private String status = "error";
        private ErrorDetail error;

        public ErrorResponse(int code, String message) {
            this.error = new ErrorDetail(code, message);
        }

        public String getStatus() { return status; }
        public ErrorDetail getError() { return error; }
    }

    static class ErrorDetail {
        private int code;
        private String message;

        public ErrorDetail(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public String getMessage() { return message; }
    }
}

