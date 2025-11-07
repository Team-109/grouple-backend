package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTests {

    @Test
    void shouldInstantiateDocumentEntity() {
        assertThat(new Document()).isNotNull();
    }
}
