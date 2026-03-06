package com.stove.drm.adapter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.jwt.JwtService;
import com.stove.drm.adapter.biz.module.jwt.JWTGenerator;
import com.stove.drm.adapter.biz.module.jwt.vo.StoveUserVo;
import com.stove.drm.adapter.core.config.DrmProp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DrmControllerTest {

    @Autowired
    MockMvc mockMvc;


    @Autowired
    DrmService drmService; // 실제 빈을 mock으로 교체
    @Autowired
    JwtService jwtService;

    @Autowired
    DrmProp drmProp;
    
    @Autowired
    JWTGenerator jwtGenerator;

    @Autowired
    ObjectMapper objectMapper;

    private final String JWT = "";
    private final String ORIGIN_FILE  = "test.xls";
    private final String ENC_FILE  = "test_Enc.xls";
    private final String DEC_FILE  = "test_Dec.xls";
    private final String ORIGIN_FILE_EXT  = "test.png";
    private final String CORRUPTED_FILE  = "test_corrupted.txt";
    private final String NAME_EMPTY_FILE  = ".xls";
    private final String NAME_SPE_FILE  = "(★)test.xls";
    private final String EMPTY_FILE  = "test_empty.txt";
    private final String LARGE_SIZE_FILE  = "test_size.txt";
    
    private byte[] getOriginFile(String fileName) throws IOException {
        Path filePath = Path.of(drmProp.getTestFile() +fileName);
        return Files.readAllBytes(filePath);
    }

    private MvcResult genMvcResult(String fileName,String url,String jwt) throws Exception {
        byte[] fileBytes = getOriginFile(fileName);
        MockMultipartFile file =
                new MockMultipartFile("file",
                        fileName,
                        MediaType.MULTIPART_FORM_DATA_VALUE,
                        fileBytes);

//        when(jwtService.isValid(null)).thenReturn(false); // Authorization 헤더 미지급 상태

       
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwt);
        MvcResult result = mockMvc.perform(
                multipart(url)
                        .file(file)
                        .headers(headers)
                        .param("drmLabel","")
                        .content(fileBytes)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();
        return result;
    }
    private MvcResult genMvcResult(String fileName,String url) throws Exception {
        StoveUserVo user = new StoveUserVo();
        user.setUserId("test");
        return genMvcResult(fileName,url,jwtGenerator.toJwtToken(user).serialize());
    }
    
    //@Test
    @DisplayName("JWT 무효 → 403 Forbidden")
    void encrypt_forbidden_when_invalid_jwt() throws Exception {

        MvcResult result = genMvcResult(ORIGIN_FILE,"/v1/drm/encrypt","eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzcGF5IiwibmFtZSI6InN3YWdnZXIgbmFtZSIsImV4cCI6MTc2NDA1NjY5MCwidXNlcklkIjoic3dhZ2dlciB1c2VyIiwiaWF0IjoxNzY0MDU2MDkwLCJ0aW1lc3RhbXAiOjE3NjQwNTYwOTA3MTZ9.9cU8ElrQqK7EGrruydaJoo1QE2lCWHRPu-l2xe8JsT8");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태확인")
                .isEqualTo(403);
        
    }

    //*--------------------------------------------------------------암호화
    @Test
    @DisplayName("정상 암호화 → 200 OK")
    void encrypt_successful() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE,"/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(ORIGIN_FILE);
        //파일 비교 → 암호화된 파일로 원본과 불일치
        assertThat(resultFileByte).as("파일비교")
                .isNotEqualTo(sourceFileByte);
    }
    
    @Test
    @DisplayName("이미 암호화 된 파일 → 200 already-encrypted")
    void encrypt_skip_when_already_decrypt() throws Exception {
        MvcResult result = genMvcResult(ENC_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(ENC_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isEqualTo(sourceFileByte);
    }

    @Test
    @DisplayName("빈 파일 암호화 요청 → 422 failed-to-encrypt")
    void encrypt_failed() throws Exception {
        MvcResult result = genMvcResult(EMPTY_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

//        // --- Body ---
//        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
//        //원본파일 가지고 오기
//        byte[] sourceFileByte = getOriginFile(EMPTY_FILE);
//
//        assertThat(resultFileByte).as("파일비교")
//                .isEqualTo(sourceFileByte);
    }

    @Test
    @DisplayName("대상 확장자가 아닌 파일 암호화 요청 → 200 원본파일 전달")
    void encrypt_failed_when_unsupported_extension() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE_EXT, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(ORIGIN_FILE_EXT);

        assertThat(resultFileByte).as("파일비교")
                .isEqualTo(sourceFileByte);
//
//        assertThat(resultFileByte).as("파일비교")
//                .isNotEqualTo(resultFileByte);
    }

    @Test
    @DisplayName("깨진 파일 암호화 → 200 OK")
    void encrypt_failed_when_corrupted() throws Exception {
        MvcResult result = genMvcResult(CORRUPTED_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);
        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(CORRUPTED_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isNotEqualTo(sourceFileByte);
    }

    @Test
    @DisplayName("파일명 NULL → 200 OK")
    void encrypt_failed_when_filename_null() throws Exception {
        MvcResult result = genMvcResult(NAME_EMPTY_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);
        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(NAME_EMPTY_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isNotEqualTo(sourceFileByte);
    }

    //@Test
    @DisplayName("파일 크기 초과 → 413 Bad Request")
    void encrypt_failed_when_filename_size() throws Exception {
        MvcResult result = genMvcResult(LARGE_SIZE_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태 : 파일 크기 초과")
                .isEqualTo(413);
    }
    
    @Test
    @DisplayName("파일명 특수 문자 → 200 OK")
    void encrypt_failed_when_filename_special() throws Exception {
        MvcResult result = genMvcResult(NAME_SPE_FILE, "/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);
        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(NAME_SPE_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isNotEqualTo(sourceFileByte);
    }
    
//*----------------------------------------------------------------------------- 복호화 
    @Test
    @DisplayName("정상 복호화 → 200 OK")
    void decrypt_successful() throws Exception {
        MvcResult result = genMvcResult(ENC_FILE,"/v1/drm/decrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);
        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(ENC_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isNotEqualTo(sourceFileByte);
    }
    
    @Test
    @DisplayName("이미 복호화 된 파일 → 200 OK")
    void decrypt_skip_when_already_plainfile() throws Exception {
        MvcResult result = genMvcResult(DEC_FILE,"/v1/drm/decrypt");
        
        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo("already-decrypted");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] resultFileByte = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] sourceFileByte = getOriginFile(DEC_FILE);

        assertThat(resultFileByte).as("파일비교")
                .isEqualTo(sourceFileByte);
    }
    
//    @Test
//    @DisplayName("복호화 불가 → 500 Internal Server Error")
//    void decrypt_failed_when_internalserver_error() throws Exception {
//        MvcResult result = genMvcResult(ENC_FILE,"/api/v1/drm/decrypt");
//
//        // --- Status ---
//        int status = result.getResponse().getStatus();
//        assertThat(status).as("상태")
//                .isEqualTo(500);
//
//        // --- Header ---
//        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
//        assertThat(authHeader).as("헤더")
//                .isEqualTo("failed-to-decrypt");
//        System.out.println("HEADER = " + authHeader);
//    }
}