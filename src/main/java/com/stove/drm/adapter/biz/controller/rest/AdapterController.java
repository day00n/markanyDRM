package com.stove.drm.adapter.biz.controller.rest;

import com.stove.drm.adapter.biz.controller.BaseRestController;
import com.stove.drm.adapter.biz.controller.vo.DoorayHeader;
import com.stove.drm.adapter.biz.controller.vo.req.DrmFileReq;
import com.stove.drm.adapter.biz.controller.vo.res.IsEncryptedRes;
import com.stove.drm.adapter.biz.controller.vo.res.QueryRightsRes;
import com.stove.drm.adapter.biz.exception.DRMException;
import com.stove.drm.adapter.biz.module.JwtService;
import com.stove.drm.adapter.biz.module.jwt.JWTGenerator;
import com.stove.drm.adapter.biz.module.jwt.vo.StoveUserVo;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.stove.drm.adapter.biz.service.DrmAdapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "암복호화 두레이 DRM 어댑터.")
@RestController
@RequestMapping("/api/v1/drm")
@RequiredArgsConstructor
public class AdapterController extends BaseRestController {

    private final JWTGenerator jWTGenerator;   // Authorization Bearer 토큰 검증
    private final JwtService jwtService;   // Authorization Bearer 토큰 검증
    private final DrmAdapterService drmAdapterService;   // 암/복호화 어댑터 연동 (외부 DRM 서버 호출)

    @Operation(
            summary  = "JWT Token을 생성한다.",
            description  = "유효시간은 10분."
    )
    @PostMapping(value = "/sampleGenJWT")
    public ResponseEntity<?> sampleGenJWT(){
        StoveUserVo stoveUserVo = new StoveUserVo();
        stoveUserVo.setUserId("swagger user");
        stoveUserVo.setName("swagger name");
        Map<String, String> rst = new HashMap<>();
        rst.put("JWT", jWTGenerator.toJwtToken(stoveUserVo).serialize());
        return jsonOk(rst);
    }

    @Operation(
            summary  = "DRM 암호화",
            description  = "multipart/form-data로 drmLabel(옵션), file(필수)을 받아 암호화합니다. " +
                    "이미 암호화된 파일이면 원문 그대로 반환하고 Dooray-Drm-Result 헤더를 추가합니다."
    )
    @PostMapping(
            value = "/encrypt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//            , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<?> encrypt(
            @RequestHeader(name = "Authorization", required = false) String authorization,  //Authorization: Bearer {jwt}
            @ModelAttribute DrmFileReq request
    ) throws IOException {
        // 1) JWT 검증: 실패 시 403
        if (!jwtService.isValid(authorization)) {
            return jsonFail(HttpStatus.FORBIDDEN,"");
        }

        // 2) 파일명가지고 오기.
        String originalName = request.getFile().getOriginalFilename();
        // 원본파일 바이너리 데이터 배열
        byte[] inputBytes = null;
        try {
            inputBytes = request.getFile().getBytes();
            // 3) 이미 암호화 파일 여부 판정
            if (drmAdapterService.isEncrypted(originalName, inputBytes)) {  //true 반환 시 원문, false 반환 시 암호화 
                // 원문 그대로 반환 + 헤더 부가 (already_encrypted)
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_encrypted.genMap());
            }
            // 4) 암호화 수행
            byte[] rst = drmAdapterService.encrypt(originalName, inputBytes);
            return fileOk(rst, originalName);
        } catch (IOException e) {
            return fileFail(HttpStatus.UNPROCESSABLE_ENTITY, null, e.getMessage());
        } catch (DRMException drmException) {
            if(drmException.getDrmErrorVo().getValue() == DrmErrorEnum.ERROR_FILE_NOT_ENCRYPTED.value()){
                //이미 암호화된 파일인 경우, 원문을 제공하며 추가 헤더를 제공합니다.
                // 원문 그대로 반환 + 헤더 부가
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_encrypted.genMap());
            }else {
                return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes,originalName, DoorayHeader.failed_to_encrypt.genMap());
            }
        }
    }

    @Operation(
            summary  = "DRM 암호화 default label ",
            description  = "multipart/form-data로 file(필수)을 받아 암호화합니다. " +
                    "이미 암호화된 파일이면 원문 그대로 반환하고 Dooray-Drm-Result 헤더를 추가합니다."
    )
    @PostMapping(
            value = "/encrypt-with-default-label",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//            , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<?> encryptWithDefaultLabel(
            @RequestHeader(name = "Authorization", required = false) String authorization,  //Authorization: Bearer {jwt}
            @ModelAttribute DrmFileReq request
    ) throws IOException {
        // 1) JWT 검증: 실패 시 403
        if (!jwtService.isValid(authorization)) {
            return jsonFail(HttpStatus.FORBIDDEN,"");
        }

        // 2) 파일명가지고 오기.
        String originalName = request.getFile().getOriginalFilename();
        // 원본파일 바이너리 데이터 배열
        byte[] inputBytes = null;
        try {
            inputBytes = request.getFile().getBytes();
            // 3) 이미 암호화 파일 여부 판정
            if (drmAdapterService.isEncrypted(originalName, inputBytes)) {  //true 반환 시 원문, false 반환 시 암호화 
                // 원문 그대로 반환 + 헤더 부가 (already_encrypted)
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_encrypted.genMap());
            }
            // 4) 암호화 수행
            byte[] rst = drmAdapterService.encrypt(originalName, inputBytes);
            return fileOk(rst, originalName);
        } catch (IOException e) {
            return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes,originalName, DoorayHeader.failed_to_decrypt.genMap());
        } catch (DRMException drmException) {
            if(drmException.getDrmErrorVo().getValue() == DrmErrorEnum.ERROR_FILE_NOT_ENCRYPTED.value()){
                //이미 암호화된 파일인 경우, 원문을 제공하며 추가 헤더를 제공합니다.
                // 원문 그대로 반환 + 헤더 부가
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_encrypted.genMap());
            }else {
                return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes,originalName, DoorayHeader.failed_to_decrypt.genMap());
            }
        }
    }
    
    @Operation(
            summary  = "DRM 복호화",
            description  = "multipart/form-data로 drm 암호화된 file(필수)을 받아 복호화합니다. " +
                    "이미 복호화된 파일이면 원문 그대로 반환하고 Dooray-Drm-Result헤더를 추가합니다."
    )
    @PostMapping(
            value = "/decrypt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//            , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<?> decrypt(
            @RequestHeader(name = "Authorization", required = false) String authorization,  //Authorization: Bearer {jwt}
            @ModelAttribute DrmFileReq request
    ) throws IOException {
        // 1) JWT 검증: 실패 시 403
        if (!jwtService.isValid(authorization)) {
            return jsonFail(HttpStatus.FORBIDDEN,"");
        }

        // 2) 파일명가지고 오기.
        String originalName = request.getFile().getOriginalFilename();
        // 원본파일 바이너리 데이터 배열
        byte[] inputBytes = null;
        try {
            inputBytes = request.getFile().getBytes();
            // 3) 암호화 파일 여부 판정
            if (!drmAdapterService.isEncrypted(originalName, inputBytes)) { //false 반환 시 원본, true 반환 시 복호화 
                // 원문 그대로 반환 + 헤더 부가 (already_encrypted)
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_decrypted.genMap());
            }
            // 4) 복호화 수행
            byte[] rst = drmAdapterService.decrypt(originalName, inputBytes);
            return fileOk(rst, originalName);
        } catch (IOException e) {
            return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes, originalName, DoorayHeader.undecryptable.genMap());
        } catch (DRMException drmException) {
            if(drmException.getDrmErrorVo().getValue() == DrmErrorEnum.ERROR_FILE_NOT_ENCRYPTED.value()){
                //이미 복호화된 파일인 경우, 원문을 제공하며 추가 헤더를 제공합니다.
                // 원문 그대로 반환 + 헤더 부가
                return fileOkWithHeader(inputBytes, originalName, DoorayHeader.already_decrypted.genMap());
            }else
                return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes, originalName, DoorayHeader.undecryptable.genMap());
        }
    }
    
    @Operation(
            summary  = "Dooray SaaS 서버가 고객사에 사용자별 복호화 권한을 조회",
            description  = "Dooray SaaS 서버가 고객사에 사용자별 복호화 권한을 조회"
    )
    @PostMapping(
            value = "/query-rights",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> queryRights(
            @RequestHeader(name = "Authorization", required = false) String authorization,  //Authorization: Bearer {jwt}
            @ModelAttribute DrmFileReq request
    ) throws IOException {


        // 1) JWT 검증: 실패 시 403sdfg
        if (!jwtService.isValid(authorization)) {
            return jsonFail(HttpStatus.FORBIDDEN,"");
        }

        QueryRightsRes rst = new QueryRightsRes();
        rst.setWritable("true");
        rst.setReadable("true");
        rst.setLabel(request.getDrmLabel());
        return jsonOk(rst);
    }
    
    @Operation(
            summary  = "Dooray SaaS 서버가 고객사 DRM 서버에 문서의 암호화 여부를 조회",
            description  = "Dooray SaaS 서버가 고객사 DRM 서버에 문서의 암호화 여부를 조회"
    )
    @PostMapping(
            value = "/is-encrypt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> isEncrypt(
            @RequestHeader(name = "Authorization", required = false) String authorization,  //Authorization: Bearer {jwt}
            @ModelAttribute DrmFileReq request
    ) {

        // 파일명 가지고 오기
        String originalName = request.getFile().getOriginalFilename();
        // 원본파일 바이너리 데이터 배열
        byte[] inputBytes = null;
        IsEncryptedRes rst = new IsEncryptedRes();
        try {
            inputBytes = request.getFile().getBytes();
            if(drmAdapterService.isEncrypted(originalName, inputBytes)){
                rst.setEncrypted("true");
            }else{
                rst.setEncrypted("false");
            }
            return jsonOk(rst);

        } catch (IOException e) {
            return fileFailWithHeader(HttpStatus.UNPROCESSABLE_ENTITY, inputBytes, originalName, DoorayHeader.undecryptable.genMap());
        }
    }
}
