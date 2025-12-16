package com.example.grouple.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomExceptionsTest {
    
    @Test
    void shouldCreateNotFoundExceptionWithMessage() {
        String message = "리소스를 찾을 수 없습니다";
        NotFoundException exception = new NotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    void shouldCreateForbiddenExceptionWithMessage() {
        String message = "권한이 없습니다";
        ForbiddenException exception = new ForbiddenException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    void shouldCreateBadRequestExceptionWithMessage() {
        String message = "잘못된 요청입니다";
        BadRequestException exception = new BadRequestException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    void shouldCreateUnauthorizedExceptionWithMessage() {
        String message = "인증되지 않은 접근입니다";
        UnauthorizedException exception = new UnauthorizedException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    void shouldCreateConflictExceptionWithMessage() {
        String message = "이미 존재하는 리소스입니다";
        ConflictException exception = new ConflictException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
