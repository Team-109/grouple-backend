package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleTests {

    @Test
    void shouldInstantiateScheduleEntity() {
        assertThat(new Schedule()).isNotNull();
    }
}
