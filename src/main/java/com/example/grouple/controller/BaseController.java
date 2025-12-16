package com.example.grouple.controller;

import com.example.grouple.common.UnauthorizedException;
import com.example.grouple.security.AuthPrincipal;

/**
 * 모든 컨트롤러의 공통 기능을 제공하는 추상 클래스
 */
public abstract class BaseController {

    /**
     * AuthPrincipal에서 userId를 안전하게 추출
     * @param principal 인증된 사용자 정보
     * @return 사용자 ID
     * @throws UnauthorizedException 인증 정보가 없거나 유효하지 않은 경우
     */
    protected Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new UnauthorizedException("인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
