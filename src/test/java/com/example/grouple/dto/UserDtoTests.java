package com.example.grouple.dto;

import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.user.response.UserImageModifyResponse;
import com.example.grouple.dto.user.response.UserModifyResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTests {

    @Test
    void userRequestDtos_shouldExposeValues() {
        UserModifyRequest modify = new UserModifyRequest();
        modify.setUsername("new");
        modify.setEmail("e@example.com");
        modify.setPhone("010");
        modify.setPassword("pw");
        assertThat(modify.getEmail()).isEqualTo("e@example.com");

        UserImageModifyForm imageForm = new UserImageModifyForm();
        imageForm.setImage("img.png");
        assertThat(imageForm.getImage()).isEqualTo("img.png");

        UserDeleteRequest deleteReq = new UserDeleteRequest();
        deleteReq.setPassword("pw");
        assertThat(deleteReq.getPassword()).isEqualTo("pw");
    }

    @Test
    void userResponseDtos_shouldExposeValues() {
        Instant now = Instant.now();
        UserModifyResponse modifyRes = new UserModifyResponse(1, "user", "e", "010", "img", now, now);
        assertThat(modifyRes.getUsername()).isEqualTo("user");

        UserImageModifyResponse imgRes = new UserImageModifyResponse("img2", now);
        assertThat(imgRes.getImage()).isEqualTo("img2");

        UserInfoResponse infoRes = new UserInfoResponse(1, "u", "e", "010", "img", now, now);
        assertThat(infoRes.getUsername()).isEqualTo("u");

        RegisterResponse regRes = new RegisterResponse(1, "u", "e", "010", "img", now, now);
        assertThat(regRes.getId()).isEqualTo(1);
    }
}
