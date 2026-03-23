package com.okfg.drm.adapter.biz.module.drm.markany;

import MarkAny.MaSaferJava.*;
//import MarkAny.MaSaferJava.Madec;
//import SCSL.*;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.okfg.drm.adapter.biz.exception.DRMException;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorVo;
import com.okfg.drm.adapter.core.config.DrmProp;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceResolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkanyDrmService {

    private final DrmProp prop;
    private final MaFileManagerService fileManagerService;
    private final ResourceLoader resourceLoader;

    /**
     * 암호화 대상 확장자 확인.
     * @param ext
     * @return
     */
    public boolean checkExt(String fileName) {
        try {
            String tmp[]= fileName.split(".");
            String ext = tmp[tmp.length-1];
            String drmExt = prop.getFileExt();
            log.info("[파일확장자체크]"+drmExt);
            for (String s : drmExt.split(";")) {
                if (s.equals(ext.toLowerCase())) {
                    return true;
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }


    public boolean isEncrypted(String fileName, byte[] inputBytes) {
        String strRetCode = "";
        Path srcPath = fileManagerService.createFile(fileName, inputBytes);//암호화 할 파일(원본파일)
        Path dstPath = srcPath.getParent().resolve(fileName + "_enc");
        try {
            //01. 임시파일 생성
            BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(srcPath));
            BufferedOutputStream outFile = new BufferedOutputStream(Files.newOutputStream(dstPath));

            BufferedInputStream inFileDec = new BufferedInputStream(Files.newInputStream(srcPath));

            //02. 암호화 여부 확인
            MaFileChk clMaFileChk = new MaFileChk(prop.getMarkanyFile());
            Madn clMadn = new Madn(prop.getMarkanyFile()); //암호화 객체
            Madec clMadec = new Madec(prop.getMarkanyFile()); //복호화 객체
            Long lFileLen = srcPath.toFile().length();
            Long OutFileLength = clMaFileChk.lGetFileChkFileSize(fileName, lFileLen, inFile);

            if(OutFileLength > 0) {
                strRetCode = clMaFileChk.strMaFileChk();
                log.info("[isEncrypted][FileCheck][파일체크시작]");
                /*
                00000 : 파일 체크 성공 --
                60042 : 암호화 파일을 암호화 시도한 경우 (암호화 파일)
                60045 : 복호화 파일을 복호화 시도한 경우 (일반 파일)
                이외 : Exception 발생 시
                */
                if (strRetCode.equals("00000")) {   //파일 체크 성공
                    log.info("[isEncrypted][FileCheck][파일체크성공] Result ::: {}", strRetCode);
                    //대상 파일을 암호화 진행 : 암호화 된 파일인경우 60042 확인
                    OutFileLength = initMarkany(srcPath, inFile, clMadn, prop);
                    log.info("[대상 파일을 암호화 진행 initMarkany]==== "+OutFileLength);

                    String retVal = clMadn.strMadn(outFile);
                    log.info("clMadn.strMadn(outFile)======");
                    if(retVal.equals("60042")){
                        log.info("[isEncrypted][Encrypted][암호화파일] Result ::: {}", strRetCode);
                        return true;
                    }
                    log.info("[retVal] "+retVal);
                    //대상 파일을 복호화 진행 : 복호화 된 파일-원문파일인 경우 60045 확인
                    OutFileLength = clMadec.lGetDecryptFileSize(fileName, lFileLen, inFileDec);
                    log.info("[대상 파일을 복호화 진행 lGetDecryptFileSize]==== "+OutFileLength);
                    retVal = clMadec.strMadec(outFile);
                    if (retVal.equals("60045")){
                        log.info("[isEncrypted][NOT Encrypted][일반파일]  Result ::: {}", strRetCode);
                        return false;
                    }
                    log.info("[retVal] "+retVal);
                } else {
                    log.debug("[FILECHECK][ErrorCode] : {} [ErrorMessage] : {}", strRetCode, clMaFileChk.strGetErrorMessage((strRetCode)));
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("[FILECHECK][암호화 여부 확인실패][FileNotFoundException] :" + e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException("[FILECHECK][암호화 여부 확인실패][IOException] :" + e.getMessage());
        } finally {
            //03. 임시파일 삭제
            if (srcPath != null){
                fileManagerService.clearTmpDir(srcPath);
            }
        }
        throw new IllegalStateException("[FILECHECK][암호화 여부 확인실패][ErrorCode] :" + strRetCode);
    }

    public byte[] encrypt(String fileName, byte[] inputBytes) throws DRMException {
        //01. 암호화 대상 여부 확인
        log.info("[파일체크]:::"+fileName);
        if (!checkExt(fileName)) return inputBytes;
        log.info("[암호화 진행]:::"+fileName);
        //02. 임시파일 생성
        Path srcPath = fileManagerService.createFile(fileName, inputBytes);//암호화 할 파일(원본파일)
        Path dstPath = srcPath.getParent().resolve(fileName + "_enc");
        log.info("[ENCRYPT] : [SRC][{}] ::: [TARGET][{}]", srcPath.toAbsolutePath(), dstPath.toAbsolutePath());

        //암호화 객체 생성
        Madn clMadn = new Madn(prop.getMarkanyFile());//파일경로 수정 필요(프로퍼티)
        String retVal;

        try {
            BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(srcPath));
            BufferedOutputStream outFile = new BufferedOutputStream(Files.newOutputStream(dstPath));
            long OutFileLength = initMarkany(srcPath, inFile, clMadn, prop);
            if (OutFileLength <= 0) {
                retVal = clMadn.strGetErrorCode();
                log.error("[ENCRYPT][ErrorCode] : {} [ErrorMessage] : {} ", retVal, clMadn.strGetErrorMessage(retVal));
            }
            //03. 암호화 진행
            retVal = clMadn.strMadn(outFile);
            int strRetCode = Integer.parseInt(retVal); //타입변환
            log.debug("[ENCRYPT][암호화 결과] : {}", strRetCode);

            if (strRetCode!=0) {
                DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(strRetCode);
                log.info("[FAIL][ENCRYPT][MARKANY] {} , {} , {} ", drmErrorVo.getCode(), drmErrorVo.getValue(), drmErrorVo.getDesc());
                throw new DRMException(strRetCode);
            }
            return Files.readAllBytes(Paths.get(dstPath.toFile().toURI())); // * 암호화 바이너리 반환
        } catch (IOException e) {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(DrmErrorEnum.FILE_IO.value());
            log.info("[FAIL][ENCRYPT][FILE IO] {} , {} , {} ", drmErrorVo.getCode(), drmErrorVo.getValue(), e.getMessage());
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        } finally {
            //04. 임시파일 삭제
//            fileManagerService.clearTmpDir(srcPath);
        }
    }

    public byte[] decrypt(String fileName, byte[] inputBytes) throws DRMException {
        //01. 임시파일 생성
        Path srcPath = fileManagerService. createFile(fileName, inputBytes); //암호화된 파일(복호화대상 파일)
        Path dstPath = srcPath.getParent().resolve(fileName + "_dec"); //복호화 완료된 파일

        log.info("[DECRYPT] [SRC:{}] [DST:{}]",srcPath.toAbsolutePath(),dstPath.toAbsolutePath());

        //02. 암호화 여부 확인
        if(!isEncrypted(fileName, inputBytes)){
            //log.info("[DECRYPT] [SRC:{}] [DST:{}]",srcPath.toAbsolutePath(),dstPath.toAbsolutePath());
            throw new DRMException(DrmErrorEnum.PLAIN_FILE_ON_DECRYPT_REQUEST.value()); //일반파일의 경우 에러
        }

        //복호화 객체 생성
        Madec clMadec = new Madec(prop.getMarkanyFile()); //파일경로 수정 필요(프로퍼티)
        String retVal;

        try {
            BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(srcPath));
            BufferedOutputStream outFile = new BufferedOutputStream(Files.newOutputStream(dstPath));
            long lFileLen = Files.size(srcPath);  //복호화 대상파일 크기
            long OutFileLength = clMadec.lGetDecryptFileSize(fileName, lFileLen, inFile); //🔴(파일명, lFileLen,inFile)

            if(OutFileLength<=0){
                retVal=clMadec.strGetErrorCode();
                log.error("[DECRYPT][ErrorCode] : {} [ErrorMessage] : {} ", retVal, clMadec.strGetErrorMessage(retVal));
            }
            //03. 복호화 진행
            retVal=clMadec.strMadec(outFile);
            int strRetCode = Integer.parseInt(retVal);
            log.debug("[DECRYPT][복호화 결과] :{}", retVal);
            //정상이 아니면 에러발생
            if(strRetCode!=0){
                DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(strRetCode);
                log.info("[FAIL][DECRYPT][MARKANY] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
//                checkResult(strRetCode);
                throw new DRMException(strRetCode);
            }return Files.readAllBytes(dstPath);
        }
         catch (IOException e) {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(DrmErrorEnum.FILE_IO.value());
            log.info("[FAIL][DECRYPT][FILE IO] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(),e.getMessage());
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        }finally {
            fileManagerService.clearTmpDir(srcPath); //임시파일 삭제
        }
    }

//    private boolean checkResult(int retVal) throws DRMException {
//        if (retVal == 0) {
//            return true;
//        } else {
//            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(retVal);
//            log.info("[FAIL][ENCRYPT][MARKANY] {} , {} , {} ", drmErrorVo.getCode(), drmErrorVo.getValue(), drmErrorVo.getDesc());
//            throw new DRMException(retVal);
//        }
//    }

    private long initMarkany(Path srcPath,BufferedInputStream inFile, Madn clMadn, DrmProp prop) throws IOException {
        long lFileLen = Files.size(srcPath);
        return clMadn.lGetEncryptFileSize(
                prop.getPiAclFlag(),        // ACL 참조 방식( 고정값 0 )
                prop.getPstrDocLevel(),     // 암호화 문서 등급 ( 고정값 0 )
                prop.getPstrUserId(),       // 사용자 ID
                srcPath.getFileName().toString(),                   // 파일 이름
                lFileLen,                    // 암호화하려는 원본 파일 크기
                prop.getPstrOwnerId(),      // 암호화 대상 파일 소유자
                prop.getStrCompanyId(),     // 회사코드 ID    "RUSHNCASH-391E-ADB5-A056" ); //🟢
                prop.getGroupId(),          // 그룹코드 ID
                prop.getPstrPositionId(),   // 직위코드 ID
                prop.getPstrGrade(),        // 등급
                prop.getPstrFileId(),       // 파일 고유 ID
                prop.getPiCanSave(),        // 저장 권한 (가능 1, 불가 0)
                prop.getPiCanEdit(),        // 수정 권한 (가능 1, 불가 0)
                prop.getPiBlockCopy(),      // 블룩복사 권한 (가능 1, 불가 0)
                prop.getPiOpenCount(),      // 열람 가능 회수 (회수 또는 제한없음 -99)
                prop.getPiPrintCount(),     // 출력 가능 회수 (회수 또는 제한없음 -99)
                prop.getPiValidPeriod(),     // 문서 사용 가능 기간(기간 또는 제한없음 -99)
                prop.getPiSaveLog(),        // 저장 로그 (가능 1, 불가 0)
                prop.getPiPrintLog(),       // 출력 로그 (가능 1, 불가 0)
                prop.getPiOpenLog(),        // 열람 로그 (가능 1, 불가 0)
                prop.getPiVisualPrint(),    // 인쇄시 워터마크 적용(적용1, 미적용 0)
                prop.getPiImageSafer(),     // 캡쳐방지 적용(적용1, 미적용 0)
                prop.getPiRealTimeAcl(),    // 사용하지 않음
                prop.getPstrDocumentTitle(),// 문서 제목
                prop.getPstrCompanyName(),  // 회사명
                prop.getPstrGroupName(),    // 그룹명
                prop.getPstrPositionName(), // 직위명
                prop.getPstrUserName(),     // 사용자 이름
                prop.getPstrUserIp(),       // 사용자 PC IP
                prop.getPstrServerOrigin(), // 시스템명
                prop.getPiExchangePolicy(), // 암호화 문서 정책 ( 고정값 1 )
                prop.getPiDrmFlag(),        // 암호화 여부( 고정값 0 )
                prop.getIBlockSize(),       // 블럭크기 ( 고정값 0 )
                prop.getStrMachineKey(),    // 머신키

                prop.getStrFileVersion(),   // 암호화 파일 버전
                prop.getStrMultiUserID(),   // 다중 사용자 ID
                prop.getStrMultiUserName(), // 다중 사용명
                prop.getStrEnterpriseID(),  // 회사 대표 ID
                prop.getStrEnterpriseName(),// 회사 대표명
                prop.getStrDeptID(),         // 부서코드 ID
                prop.getStrDeptName(),       // 부서명
                prop.getStrPositionLevel(), // 직위레벨
                prop.getStrSecurityLevel(), // 보안레벨
                prop.getStrSecurityLevelName(),// 보안레벨명
                prop.getStrPgCode(),        // 사용하지 않음
                prop.getStrCipherBlockSize(), // 사이퍼블럭크기 ( 고정값 16 )
                prop.getStrCreatorID(),     // 생성자 ID
                prop.getStrCreatorName(),   // 생성자 이름
                prop.getStrOnlineControl(), // 고정값 0
                prop.getStrOfflinePolicy(), // 고정값
                prop.getStrValidPeriodType(),// 고정값
                prop.getStrUsableAlways(),  // 고정값 0
                prop.getStrPriPubKey(),     // 고정값
                prop.getStrCreatorCompanyId(), // 생성자 회사코드 ID
                prop.getStrCreatorDeptId(), // 생성자 부서코드 ID
                prop.getStrCreatorGroupId(),// 생성자 그룹코드 ID
                prop.getStrCreatorPositionId(),// 생성자 직위코드 ID
                prop.getStrFileSize(),      // 원본파일크기
                prop.getStrHeaderUpdateTime(),//	헤더업데이트시간
                prop.getStrReserved01(),// 지정 필드1
                prop.getStrReserved02(),// 지정 필드2
                prop.getStrReserved03(),// 지정 필드3
                prop.getStrReserved04(),// 지정 필드4
                prop.getStrReserved05(),// 지정 필드5
                inFile //암호화 스트림파일
        );//🔴인수값
    }

/**
    @Autowired
    public long getEncryptFileSize(String fileName, long lfileLen, BufferedInputStream inFile) {
        return clMadn.lGetEncryptFileSize(
                prop.getPiAclFlag(),        // ACL 참조 방식( 고정값 0 )
                prop.getPstrDocLevel(),     // 암호화 문서 등급 ( 고정값 0 )
                prop.getPstrUserId(),       // 사용자 ID
                fileName,                   // 파일 이름
                lfileLen,                    // 암호화하려는 원본 파일 크기
                prop.getPstrOwnerId(),      // 암호화 대상 파일 소유자
                prop.setStrCompanyId("RUSHNCASH-391E-ADB5-A056"),     // 회사코드 ID    "RUSHNCASH-391E-ADB5-A056" ); //🟢
                prop.getGroupId(),          // 그룹코드 ID
                prop.getPstrPositionId(),   // 직위코드 ID
                prop.getPstrGrade(),        // 등급
                prop.getPstrFileId(),       // 파일 고유 ID
                prop.getPiCanSave(),        // 저장 권한 (가능 1, 불가 0)
                prop.getPiCanEdit(),        // 수정 권한 (가능 1, 불가 0)
                prop.getPiBlockCopy(),      // 블룩복사 권한 (가능 1, 불가 0)
                prop.getPiOpenCount(),      // 열람 가능 회수 (회수 또는 제한없음 -99)
                prop.getPiPrintCount(),     // 출력 가능 회수 (회수 또는 제한없음 -99)
                prop.getPiPrintCount(),     // 문서 사용 가능 기간(기간 또는 제한없음 -99)
                prop.getPiSaveLog(),        // 저장 로그 (가능 1, 불가 0)
                prop.getPiPrintLog(),       // 출력 로그 (가능 1, 불가 0)
                prop.getPiOpenLog(),        // 열람 로그 (가능 1, 불가 0)
                prop.getPiVisualPrint(),    // 인쇄시 워터마크 적용(적용1, 미적용 0)
                prop.getPiImageSafer(),     // 캡쳐방지 적용(적용1, 미적용 0)
                prop.getPiRealTimeAcl(),    // 사용하지 않음
                prop.getPstrDocumentTitle(),// 문서 제목
                prop.getPstrCompanyName(),  // 회사명
                prop.getPstrGroupName(),    // 그룹명
                prop.getPstrPositionName(), // 직위명
                prop.getPstrUserName(),     // 사용자 이름
                prop.getPstrUserIp(),       // 사용자 PC IP
                prop.getPstrServerOrigin(), // 시스템명
                prop.getPiExchangePolicy(), // 암호화 문서 정책 ( 고정값 1 )
                prop.getPiDrmFlag(),        // 암호화 여부( 고정값 0 )
                prop.getIBlockSize(),       // 블럭크기 ( 고정값 0 )
                prop.getStrMachineKey(),    // 머신키

                prop.getStrFileVersion(),   // 암호화 파일 버전
                prop.getStrMultiUserID(),   // 다중 사용자 ID
                prop.getStrMultiUserName(), // 다중 사용명
                prop.setStrEnterpriseID("RUSHNCASHG-B0A4-894C-2638"),  // 회사 대표 ID
                prop.getStrEnterpriseName(),// 회사 대표명
                prop.getStrDeptID(),         // 부서코드 ID
                prop.getStrDeptName(),       // 부서명
                prop.getStrPositionLevel(), // 직위레벨
                prop.getStrSecurityLevel(), // 보안레벨
                prop.getStrSecurityLevelName(),// 보안레벨명
                prop.getStrPgCode(),        // 사용하지 않음
                prop.getStrCipherBlockSize(), // 사이퍼블럭크기 ( 고정값 16 )
                prop.getStrCreatorID(),     // 생성자 ID
                prop.getStrCreatorName(),   // 생성자 이름
                prop.getStrOnlineControl(), // 고정값 0
                prop.getStrOfflinePolicy(), // 고정값
                prop.getStrValidPeriodType(),// 고정값
                prop.getStrUsableAlways(),  // 고정값 0
                prop.getStrPriPubKey(),     // 고정값
                prop.getStrCreatorCompanyId(), // 생성자 회사코드 ID
                prop.getStrCreatorDeptId(), // 생성자 부서코드 ID
                prop.getStrCreatorGroupId(),// 생성자 그룹코드 ID
                prop.getStrCreatorPositionId(),// 생성자 직위코드 ID
                prop.getStrFileSize(),      // 원본파일크기
                prop.getStrHeaderUpdateTime(),//	헤더업데이트시간
                prop.getStrReserved01(),// 지정 필드1
                prop.getStrReserved02(),// 지정 필드2
                prop.getStrReserved03(),// 지정 필드3
                prop.getStrReserved04(),// 지정 필드4
                prop.getStrReserved05(),// 지정 필드5
                inFile //암호화 스트림파일
        );
    }*/
}