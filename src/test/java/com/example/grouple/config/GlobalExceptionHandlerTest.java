package com.example.grouple.config;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.common.BadRequestException;
import com.example.grouple.common.ConflictException;
import com.example.grouple.common.ForbiddenException;
import com.example.grouple.common.NotFoundException;
import com.example.grouple.common.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler handler;
    
    @Test
    void shouldHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("잘못된 요청입니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleBadRequest(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().message())
            .isEqualTo("잘못된 요청입니다");
    }
    
    @Test
    void shouldHandleUnauthorizedException() {
        UnauthorizedException exception = 
            new UnauthorizedException("인증되지 않은 접근입니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleUnauthorized(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().code()).isEqualTo(401);
    }
    
    @Test
    void shouldHandleForbiddenException() {
        ForbiddenException exception = 
            new ForbiddenException("권한이 없습니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleForbidden(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().code()).isEqualTo(403);
        assertThat(response.getBody().getError().message())
            .isEqualTo("권한이 없습니다");
    }
    
    @Test
    void shouldHandleNotFoundException() {
        NotFoundException exception = 
            new NotFoundException("리소스를 찾을 수 없습니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleNotFound(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().code()).isEqualTo(404);
    }
    
    @Test
    void shouldHandleConflictException() {
        ConflictException exception = 
            new ConflictException("이미 존재하는 리소스입니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleConflict(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().code()).isEqualTo(409);
    }
    
    @Test
    void shouldHandleGenericException() {
        Exception exception = new Exception("서버 오류");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleServer(exception);
        
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().code()).isEqualTo(500);
    }
    
    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException exception = 
            new IllegalArgumentException("잘못된 인자입니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleBadRequest(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    void shouldHandleNoSuchElementException() {
        NoSuchElementException exception = 
            new NoSuchElementException("요소를 찾을 수 없습니다");
        
        ResponseEntity<ApiResponse<Void>> response = 
            handler.handleNotFound(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
