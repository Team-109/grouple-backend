package com.example.grouple.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptTests {

    private final LocalDate MOCK_DATE = LocalDate.of(2025, 12, 1);
    private final Instant MOCK_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    // 테스트에 필요한 가짜 Organization/User 객체를 생성하는 헬퍼 메서드
    private Organization createMockOrganization(Integer id) {
        Organization org = new Organization();
        org.setId(id);
        org.setCode(UUID.randomUUID().toString().substring(0, 6));
        return org;
    }

    private User createMockUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testuser" + id);
        return user;
    }

    // --- 1. 기본 필드 설정 테스트 ---

    @Test
    @DisplayName("영수증 기본 필드가 올바르게 설정되는지 확인")
    void shouldSetAllBasicFieldsCorrectly() {
        // WHEN
        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setType("식비");
        receipt.setAmount(15000);
        receipt.setCategory("식사");
        receipt.setDescription("점심 식사");
        receipt.setDate(MOCK_DATE);

        // THEN
        assertThat(receipt.getId()).isEqualTo(1);
        assertThat(receipt.getType()).isEqualTo("식비");
        assertThat(receipt.getAmount()).isEqualTo(15000);
        assertThat(receipt.getCategory()).isEqualTo("식사");
        assertThat(receipt.getDescription()).isEqualTo("점심 식사");
        assertThat(receipt.getDate()).isEqualTo(MOCK_DATE);
    }

    // --- 2. 관계(Relation) 설정 테스트 ---

    @Test
    @DisplayName("Organization 엔티티 관계 설정 확인")
    void shouldHoldOrganizationRelationship() {
        // GIVEN
        Organization mockOrganization = createMockOrganization(20);
        Receipt receipt = new Receipt();

        // WHEN
        receipt.setOrganization(mockOrganization);

        // THEN (OrganizationTests의 shouldHoldOwnerRelationship과 유사)
        assertThat(receipt.getOrganization()).isNotNull();
        assertThat(receipt.getOrganization().getId()).isEqualTo(20);
    }

    @Test
    @DisplayName("User 엔티티 관계 설정 확인")
    void shouldHoldUserRelationship() {
        // GIVEN
        User mockUser = createMockUser(30);
        Receipt receipt = new Receipt();

        // WHEN
        receipt.setUser(mockUser);

        // THEN
        assertThat(receipt.getUser()).isNotNull();
        assertThat(receipt.getUser().getId()).isEqualTo(30);
    }

    @Test
    void shouldBuildReceiptWithBuilder() {
        Organization org = createMockOrganization(50);
        User user = createMockUser(60);

        Receipt receipt = Receipt.builder()
                .type("CARD")
                .amount(5000)
                .category("FOOD")
                .description("lunch")
                .date(MOCK_DATE)
                .image("img.png")
                .organization(org)
                .user(user)
                .build();

        assertThat(receipt.getType()).isEqualTo("CARD");
        assertThat(receipt.getAmount()).isEqualTo(5000);
        assertThat(receipt.getCategory()).isEqualTo("FOOD");
        assertThat(receipt.getDescription()).isEqualTo("lunch");
        assertThat(receipt.getDate()).isEqualTo(MOCK_DATE);
        assertThat(receipt.getImage()).isEqualTo("img.png");
        assertThat(receipt.getOrganization()).isSameAs(org);
        assertThat(receipt.getUser()).isSameAs(user);
    }
}
