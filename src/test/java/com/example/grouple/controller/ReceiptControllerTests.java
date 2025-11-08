package com.example.grouple.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptControllerTests {

    @Test
    void shouldInstantiateController() {
        assertThat(new OrgReceiptController()).isNotNull();
    }
}
