package com.example.grouple.controller;

import com.example.grouple.common.UnauthorizedException;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BaseControllerTest {
    
    private BaseController controller = new BaseController() {};
    
    @Test
    void shouldExtractUserIdFromValidPrincipal() {
        AuthPrincipal principal = new AuthPrincipal(123, "testuser");
        
        Integer userId = controller.requireUserId(principal);
        
        assertThat(userId).isEqualTo(123);
    }
    
    @Test
    void shouldThrowUnauthorizedExceptionWhenPrincipalIsNull() {
        assertThrows(UnauthorizedException.class, 
            () -> controller.requireUserId(null));
    }
    
    @Test
    void shouldThrowUnauthorizedExceptionWhenUserIdIsNull() {
        AuthPrincipal principal = new AuthPrincipal(null, "testuser");
        
        assertThrows(UnauthorizedException.class, 
            () -> controller.requireUserId(principal));
    }
    
    @Test
    void shouldThrowUnauthorizedExceptionWithCorrectMessage() {
        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class, 
            () -> controller.requireUserId(null)
        );
        
        assertThat(exception.getMessage())
            .isEqualTo("인증 정보를 확인할 수 없습니다.");
    }
}
