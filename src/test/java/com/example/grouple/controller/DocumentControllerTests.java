package com.example.grouple.controller;

import com.example.grouple.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentControllerTests {

    @Test
    void shouldInstantiateController() {
        // 1️⃣ DocumentService를 Mock으로 생성
        DocumentService mockService = Mockito.mock(DocumentService.class);

        // 2️⃣ Mock을 생성자에 넣어서 컨트롤러 생성
        OrgDocumentController controller = new OrgDocumentController(mockService);

        // 3️⃣ 컨트롤러가 정상 생성됐는지 확인
        assertThat(controller).isNotNull();
    }
}
