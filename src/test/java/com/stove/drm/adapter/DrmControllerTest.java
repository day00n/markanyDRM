package com.stove.drm.adapter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.JwtService;
import com.stove.drm.adapter.core.config.DrmProp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DrmControllerTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    DrmService drmService; // 실제 빈을 mock으로 교체
    @MockBean
    JwtService jwtService;

    @Autowired
    DrmProp drmProp;

    @Autowired
    ObjectMapper objectMapper;

    private final String JWT = "";
    private final String ORIGIN_FILE  = "test_origin.xlsx";
    private final String ENC_FILE  = "test_origin.xlsx";

    @Test
    @DisplayName("JWT 무효 → 403 Forbidden")
    void encrypt_forbidden_when_invalid_jwt() throws Exception {
        Path filePath = Path.of(drmProp.getTestFile() +ORIGIN_FILE);
        byte[] fileBytes = Files.readAllBytes(filePath);
        MockMultipartFile file =
                new MockMultipartFile("file",
                        filePath.getFileName().toString(),
                        MediaType.APPLICATION_OCTET_STREAM.toString(),
                        fileBytes);

//        when(jwtService.isValid(null)).thenReturn(false); // Authorization 헤더 미지급 상태

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+JWT);
        MvcResult result = mockMvc.perform(
                multipart("/api/v1/drm/encrypt")
                        .file(file)
                        .headers(headers)
                        .param("drmLabel","")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태확인")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        String body = result.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(body,
                new TypeReference<Map<String, Object>>() {});
        System.out.println("BODY = " + body);

    }

}