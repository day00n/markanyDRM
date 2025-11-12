package com.stove.drm.adapter;


import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DrmControllerTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    DrmService drmService; // 실제 빈을 mock으로 교체
    @MockBean
    JwtService jwtService;

    @Test
    @DisplayName("JWT 무효 → 403 Forbidden")
    void encrypt_forbidden_when_invalid_jwt() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "plain.txt", "text/plain", "hello".getBytes());

//        when(jwtService.isValid(null)).thenReturn(false); // Authorization 헤더 미지급 상태

        mockMvc.perform(
                multipart("/api/v1/sample/drm/encrypt")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("JWT 무효111 → 403 Forbidden")
    void encrypt_forbidden_when_invalid_jwt1() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "plain.txt", "text/plain", "hello".getBytes());

//        when(jwtService.isValid(null)).thenReturn(false); // Authorization 헤더 미지급 상태

        mockMvc.perform(
                multipart("/api/v1/sample/drm/encrypt")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isForbidden());
    }
}