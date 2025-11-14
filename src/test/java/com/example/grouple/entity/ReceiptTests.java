package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptTests {

    @Test
    void shouldInstantiateReceiptEntity() {
        assertThat(new Receipt()).isNotNull();
    }
}
