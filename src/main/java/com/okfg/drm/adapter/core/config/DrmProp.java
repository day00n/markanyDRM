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

    private String markanydata;

    private int		piAclFlag = 0;
    private String	pstrDocLevel = "0";
    private String	pstrUserId = "test_id";
    private String	pstrFileName = "testFile.xls";
    private String  plFileSize = "0";
    private String	pstrOwnerId = "pstrOwnerId";
    private String  strCompanyId = "RUSHNCASH-391E-ADB5-A056"; //🟢
    private String	pstrGroupId = "pstrGroupId" ;
    private String	pstrPositionId = "pstrPositionId" ;
    private String	pstrGrade = "pstrGrade" ;
    private String	pstrFileId = "20100804165120000abcedfasdf20100804165120000abcedfasdf20100804165120000abcedfasdf20100804165120000abcedfasdf20100804165120000abcedfasdf_endofstring";
    private int		piCanSave = 0;
    private int		piCanEdit = 0;
    private int		piBlockCopy = 0;
    private int		piOpenCount = -99;
    private int		piPrintCount = -99;
    private int		piValidPeriod = -99;
    private int		piSaveLog = 1;
    private int		piPrintLog = 1;
    private int		piOpenLog = 1;
    private int		piVisualPrint = 1;
    private int		piImageSafer = 1;
    private int		piRealTimeAcl = 0;
    private String	pstrDocumentTitle = "";
    private String	pstrCompanyName = "MarkAny";
    private String	pstrGroupName = "pstrGroupName";
    private String	pstrPositionName = "pstrPositionName";
    private String	pstrUserName = "drm001";
    private String	pstrUserIp = "127.0.0.1";
    private String	pstrServerOrigin = "대한민국";
    private int		piExchangePolicy = 1;
    private int		piDrmFlag = 0;
    private int		iBlockSize = 0;
    private String	strMachineKey = "";

    private String	strFileVersion = "" ;
    private String	strMultiUserID = "userid;userid2;length_test;userid_markany1234567890;userid_markany1234567890;userid_markany1234567890;userid_markany1234567890;userid_markany1234567890;long_userid_test;" ;
    private String	strMultiUserName = "strSecurityLevelName;multiusername1;multiusername2;multiusername3;multiusername4;multiusername5;multiusername6;multiusername7;multiusername8;multiusername9;multiusername10;" ;
    private String  strEnterpriseID = "RUSHNCASHG-B0A4-894C-2638"; //🟢
    private String	strEnterpriseName = "";
    private String	strDeptID = "deptid;deptid1;deptid2;deptid3;deptid4;deptid5;deptid6;deptid7;aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa;end_of_string" ;
    private String	strDeptName = "deptname;deptname1;deptname2;deptname3;deptname4;deptname5;deptname6;deptname7;1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111;end_of_string" ;
    private String	strPositionLevel = "";
    private String	strSecurityLevel = "1";
    private String	strSecurityLevelName = "";
    private String	strPgCode = "";
    private String	strCipherBlockSize = "16";
    private String	strCreatorID = "";
    private String	strCreatorName = "";
    private String	strOnlineControl = "0";
    private String	strOfflinePolicy = "";
    private String	strValidPeriodType = "";
    private String	strUsableAlways = "0";
    private String	strPriPubKey = "";
    private String	strCreatorCompanyId ="mark1";
    private String	strCreatorDeptId ="mark2";
    private String	strCreatorGroupId = "mark3";
    private String	strCreatorPositionId = "mark4";
    private String	strFileSize = "4567";
    private String	strHeaderUpdateTime = "" ;
    private String	strReserved01 = "reserved01" ;
    private String	strReserved02 = "reserved02" ;
    private String	strReserved03 = "reserved03" ;
    private String	strReserved04 = "reserved04" ;
    private String	strReserved05 = "reserved05" ;
}