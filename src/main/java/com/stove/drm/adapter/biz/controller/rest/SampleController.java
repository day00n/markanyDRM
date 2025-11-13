package com.stove.drm.adapter.biz.controller.rest;

import com.stove.drm.adapter.biz.controller.BaseRestController;

import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "테스트를 위해 사용자 토큰을 생성.")
@RestController
@RequestMapping("/api/v1/sample/drm")
@RequiredArgsConstructor
public class SampleController extends BaseRestController {

    private final JwtService jwtService;   // Authorization Bearer 토큰 검증
    private final DrmService drmService;   // 암/복호화 어댑터 연동 (외부 DRM 서버 호출)

    @Operation(
            summary  = "DRM 암호화",
            description  = "multipart/form-data로 drmLabel(옵션), file(필수)을 받아 암호화합니다. " +
                    "이미 암호화된 파일이면 원문 그대로 반환하고 Dooray-Drm-Result 헤더를 추가합니다."
    )
    @PostMapping(
            value = "/encrypt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> encrypt(
            @RequestHeader(name = "Authorization", required = false) String authorization,

            @Parameter(description = "DRM 라벨 (옵션)", required = false)
            @RequestParam(name = "drmLabel", required = false) String drmLabel,

            @Parameter(description = "암/복호화 대상 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        // 1) JWT 검증: 실패 시 403
        if (!jwtService.isValid(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        // 2) 파일명/타입 준비
        String originalName = file.getOriginalFilename();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(originalName).build());

//        try {
//            byte[] inputBytes = file.getBytes();
//
//            // 3) 이미 암호화 파일 여부 판정
//            if (drmService.isEncrypted(inputBytes)) {
//                // 원문 그대로 반환 + 헤더 부가
//                headers.add("Dooray-Drm-Result", "already-encrypted");
//                return new ResponseEntity<>(new ByteArrayResource(inputBytes), headers, HttpStatus.OK);
//            }
//
//            // 4) 암호화 수행 (drmLabel 옵션 전달)
//            byte[] encrypted = drmService.encrypt(inputBytes, drmLabel);
//
//            // 결과 반환
//            return new ResponseEntity<>(new ByteArrayResource(encrypted), headers, HttpStatus.OK);
//
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            // DRM 서버 오류 등
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
    }
}
