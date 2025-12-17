package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTests {

    @Test
    void shouldInstantiateDocumentEntity() {
        assertThat(new Document()).isNotNull();
    }

    @Test
    void shouldSetFieldsAndRelationsViaBuilder() {
        User user = new User();
        user.setId(5);
        user.setUsername("user");
        Organization org = new Organization();
        org.setId(7);
        org.setName("Org");

        Document document = Document.builder()
                .title("title")
                .description("desc")
                .name("file.pdf")
                .type("pdf")
                .size(100)
                .user(user)
                .organization(org)
                .build();

        assertThat(document.getTitle()).isEqualTo("title");
        assertThat(document.getDescription()).isEqualTo("desc");
        assertThat(document.getName()).isEqualTo("file.pdf");
        assertThat(document.getType()).isEqualTo("pdf");
        assertThat(document.getSize()).isEqualTo(100);
        assertThat(document.getUser()).isSameAs(user);
        assertThat(document.getOrganization()).isSameAs(org);
    }
}
