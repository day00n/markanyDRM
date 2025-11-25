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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

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
    private final String ORIGIN_FILE  = "test.xls";
    private final String ENC_FILE  = "test_Enc.xls";
    private final String DEC_FILE  = "test_Dec.xls";
    private final String ENC_FILE_EXT  = "test_Enc.xlls";
    private final String ORIGIN_FILE_EXT  = "test.xlls";
    private final String CORRUPTED_FILE  = "test_corrupted.txt";
    private final String NAME_EMPTY_FILE  = ".xls";
    private final String NAME_SPE_FILE  = "(★)test.xls";
    

    private byte[] getOriginFile(String fileName) throws IOException {
        Path filePath = Path.of(drmProp.getTestFile() +fileName);
        return Files.readAllBytes(filePath);
    }
    
    private MvcResult genMvcResult(String fileName,String url) throws Exception {
        byte[] fileBytes = getOriginFile(fileName);
        MockMultipartFile file =
                new MockMultipartFile("file",
                        fileName,
                        MediaType.MULTIPART_FORM_DATA_VALUE,
                        fileBytes);

//        when(jwtService.isValid(null)).thenReturn(false); // Authorization 헤더 미지급 상태

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+JWT);
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
    
    @Test
    @DisplayName("JWT 무효 → 403 Forbidden")
    void encrypt_forbidden_when_invalid_jwt() throws Exception {

        MvcResult result = genMvcResult(ORIGIN_FILE,"/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태확인")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" Forbidden");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        String body = result.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(body,
                new TypeReference<Map<String, Object>>() {});
        System.out.println("BODY = " + body);
    }

    //*--------------------------------------------------------------암호화
    @Test
    @DisplayName("정상 암호화 → 200 OK")
    void encrypt_successful() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE,"/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" OK");
        System.out.println("HEADER = " + authHeader);
    }
    
    @Test
    @DisplayName("이미 암호화 된 파일 → 200 already-encrypted")
    void encrypt_skip_when_already_decrypt() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" already-encrypted");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(ORIGIN_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

    @Test
    @DisplayName("암호화 실패 → 400 failed-to-encrypt")
    void encrypt_failed() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(400);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-encrypt");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(ORIGIN_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

    @Test
    @DisplayName("암호화 불가(파일대상자) → 422 undecryptable")
    void encrypt_failed_when_unsupported_extension() throws Exception {
        MvcResult result = genMvcResult(ORIGIN_FILE_EXT, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-encrypt");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(ORIGIN_FILE_EXT);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

    @Test
    @DisplayName("깨진 파일 암호화 → 422 undecryptable")
    void encrypt_failed_when_corrupted() throws Exception {
        MvcResult result = genMvcResult(CORRUPTED_FILE, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-encrypt");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(CORRUPTED_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

    @Test
    @DisplayName("파일명 NULL → 422 undecryptable")
    void encrypt_failed_when_filename_null() throws Exception {
        MvcResult result = genMvcResult(NAME_EMPTY_FILE, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-encrypt");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(NAME_EMPTY_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

//    @Test
//    @DisplayName("파일 크기 초과 → 400 Bad Request")
//    void encrypt_failed_when_filename_size() throws Exception {
//        MvcResult result = genMvcResult(NAME_SPE_FILE, "/api/v1/drm/encrypt");
//
//        // --- Status ---
//        int status = result.getResponse().getStatus();
//        assertThat(status).as("상태 : 파일 크기 초과")
//                .isEqualTo(422);
//
//        // --- Header ---
//        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
//        assertThat(authHeader).as("헤더")
//                .isEqualTo(" failed-to-encrypt");
//        System.out.println("HEADER = " + authHeader);
//
//        // --- Body ---
//        byte[] body = result.getResponse().getContentAsByteArray();
//        //원본파일 가지고 오기
//        byte[] fileBytes = getOriginFile(NAME_SPE_FILE);
//        if (body == fileBytes)
//            System.out.println("BODY = " + body);
//    }
    
    @Test
    @DisplayName("파일명 특수 문자 → 422 undecryptable")
    void encrypt_failed_when_filename_special() throws Exception {
        MvcResult result = genMvcResult(NAME_SPE_FILE, "/api/v1/drm/encrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-encrypt");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(NAME_SPE_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }

    
//*----------------------------------------------------------------------------- 복호화 
    @Test
    @DisplayName("정상 복호화 → 200 OK")
    void decrypt_successful() throws Exception {
        MvcResult result = genMvcResult(ENC_FILE,"/api/v1/drm/decrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" OK");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(ENC_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }
    

    @Test
    @DisplayName("이미 복호화 된 파일 → 200 OK")
    void decrypt_skip_when_already_plainfile() throws Exception {
        MvcResult result = genMvcResult(DEC_FILE,"/api/v1/drm/decrypt");
        
        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(200);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" already-decrypted");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(DEC_FILE);
        if (body == fileBytes)
            System.out.println("BODY = " + body);
    }
    
    @Test
    @DisplayName("복호화 불가(파일대상자) → 422 undecryptable")
    void decrypt_undecryptable_when_unsupported_extension() throws Exception {
        MvcResult result = genMvcResult(ENC_FILE_EXT,"/api/v1/drm/decrypt");
        
        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(422);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" undecryptable");
        System.out.println("HEADER = " + authHeader);

        // --- Body ---
        byte[] body = result.getResponse().getContentAsByteArray();
        //원본파일 가지고 오기
        byte[] fileBytes = getOriginFile(ENC_FILE_EXT);
        if (body == fileBytes)
            System.out.println("BODY = " + body);;
    }

    @Test
    @DisplayName("복호화 불가 → 500 Internal Server Error")
    void decrypt_failed_when_internalserver_error() throws Exception {
        MvcResult result = genMvcResult(ENC_FILE,"/api/v1/drm/decrypt");

        // --- Status ---
        int status = result.getResponse().getStatus();
        assertThat(status).as("상태")
                .isEqualTo(500);

        // --- Header ---
        String authHeader = result.getResponse().getHeader("Dooray-Drm-Result");
        assertThat(authHeader).as("헤더")
                .isEqualTo(" failed-to-decrypt");
        System.out.println("HEADER = " + authHeader);

    }
}