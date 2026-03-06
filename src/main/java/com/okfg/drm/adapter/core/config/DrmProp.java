package com.okfg.drm.adapter.core.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "drm")
@Getter
@Setter
public class DrmProp {
    private Jwt jwt = new Jwt();

    private String testFile;
    private String softcampProperties;
    private String markanyProperties;
    /**
     * 생성자 정보에 연동시스템 이름이 들어감.
     * EX) SECURITYDOMAIN
     */
    private String defaultDomain;

    /**
     * 복호화를 위한 키파일.
     */
    private String keyfilePath;
    private String groupId;
    /**
     * 키구분(1:그룹 , 0:개인),읽기 권한,수정 권한,복호화 권한,외부전송권한,프린트권한,마킹유무,자동파기,권한변경
     * 1 : 권한있음
     * 0 : 권한없음
     * EX) 111001100
     */
    private String defaultAuthBits;
    /**
     * 지원확장자 ( 구분자는 ';', 소문자를 사용 )
     * doc;docx;xls;xlsx;xlsm;ppt;pptx;hwp;hwpx;pdf;bmp;gif;jpg;jpeg;tif;tiff;csv;txt
     */
    private String fileExt;
    private String doorayFilePath;

    @Data
    public class Jwt{
        private String secret;
        private String issuer;
    }
}