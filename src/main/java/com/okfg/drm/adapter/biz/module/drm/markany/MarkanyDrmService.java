package com.okfg.drm.adapter.biz.module.drm.markany;

import MarkAny.MaSaferJava.*;
//import MarkAny.MaSaferJava.Madec;
//import SCSL.*;
import com.okfg.drm.adapter.biz.exception.DRMException;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorVo;
import com.okfg.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkanyDrmService {

    private final DrmProp prop;
    private final MaFileManagerService fileManagerService;

    /**
     * 마크애니 프로퍼티 호출.
     * @return
     * @throws IOException
     */
    private String getPropPath(){
        Path path = Path.of(prop.getMarkanyProperties());
        log.info("[PROP PATH] ::: {}",path.toAbsolutePath().toString());
        return path.toAbsolutePath().toString();
    }
    private String getKeyPath(){
        Path path = Path.of(prop.getKeyfilePath());
        return path.toAbsolutePath().toString();
    }

    /**
     * 암호화 대상 확장자 확인.
     * @param fileName
     * @return
     */
    public boolean checkExt(String fileName) {
        log.info("[CHECK EXT] {}",fileName);
        MaFileChk clMaFileChk = new MaFileChk("MarkAny.dat");
        String strRetCode="";
        //sFile.SettingPathForProperty(getPropPath());
        /*
        1 : 지원되는 확장자
        0 : 지원되지 않는 확장자
         */
        //int ret = sFile.DSIsSupportFile(fileName);
        if(!strRetCode.equals("")){
            return true;
        }
        log.info("[대상아님][ sFile.DSIsSupportFile] Result ::: {}",clMaFileChk.strGetErrorMessage(strRetCode)); //🔴확장자 확인 필요
        return false;
        
    }

    public boolean isEncrypted(String fileName, byte[] inputBytes) {
        Path path = null;
        String strRetCode = "";
        try {
            //01. 임시파일 생성
            path = fileManagerService.createFile(fileName, inputBytes);
            BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(path.toFile()));

            //02. 암호화 여부 확인
            MaFileChk clMaFileChk = new MaFileChk("MarkAny.dat"); //🔴 /home/onboarding/SAFER45/java

            Long lFileLen = path.toFile().length();
            Long OutFileLength = clMaFileChk.lGetFileChkFileSize(fileName, lFileLen, inFile);

            if(OutFileLength > 0){
                strRetCode = clMaFileChk.strMaFileChk();
                /*
                60042 : 암호화 파일을 암호화 시도한 경우 (암호화 파일) //🔴암호화 파일 == 00000? 확인
                60045 : 복호화 파일을 복호화 시도한 경우 (일반 파일)
                이외 : Exception 발생 시
                */
                if(strRetCode.equals("60042")){
                    log.info("[Madn.isEncrypted][Encrypted][암호화파일] Result ::: {}",strRetCode);
                    return true;
                } else if (strRetCode.equals("60045")) {
                    log.info("[Madn.isEncrypted][NOT Encrypted][일반파일]  Result ::: {}",strRetCode);
                    return false;
                }
            }else{
                log.debug("[FILECHECK] [ErrorCode] : {} [ErrorMessage] : {}", strRetCode, clMaFileChk.strGetErrorMessage((strRetCode)));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            //03. 임시파일 삭제
            if (path != null){
                fileManagerService.clearTmpDir(path);
            }
        }
        throw new IllegalStateException("[FILECHECK][암호화 여부 확인실패][ErrorCode] :" + strRetCode);
    }

    public byte[] encrypt(String fileName, byte[] inputBytes) throws DRMException {
        //01. 암호화 대상 여부 확인
        if (!checkExt(fileName)) return inputBytes;

        //02. 임시파일 생성
        Path srcPath = fileManagerService.createFile(fileName, inputBytes);//암호화 할 파일(원본파일)
        Path dstPath = srcPath.getParent().resolve(fileName + "_enc");
        log.info("[ENCRYPT] : [SRC][{}] ::: [TARGET][{}]", srcPath.toAbsolutePath(), dstPath.toAbsolutePath());

        //암호화 객체 생성
        Madn clMadn = new Madn("/MarkAnyServerInfo.dat");//파일경로 수정 필요(프로퍼티)
        //long lFileLen = Files.size(srcPath);  //암호화 대상파일 크기
        String retVal;

        try (BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(srcPath));
             BufferedOutputStream outFile = new BufferedOutputStream(Files.newOutputStream(dstPath))) {
            //long lFileLen = Files.size(srcPath);  //암호화 대상파일 크기
            long OutFileLength = clMadn.lGetEncryptFileSize(srcPath.toFile(), inFile); //🔴인수값 65개 마지막값 암호화 스트림파일
            if (OutFileLength <= 0) {
                retVal = clMadn.strGetErrorCode();
                log.error("[ENCRYPT][ErrorCode] : {} [ErrorMessage] : {} ", retVal, clMadn.strGetErrorMessage(retVal));
            }
            //03. 암호화 진행
            retVal = clMadn.strMadn(outFile);
            int strRetCode = Integer.parseInt(retVal); //타입변환
            log.debug("[ENCRYPT][암호화 결과] : {}", strRetCode);

            if (strRetCode==0) {
                DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(strRetCode);
                log.info("[FAIL][ENCRYPT][MARKANY] {} , {} , {} ", drmErrorVo.getCode(), drmErrorVo.getValue(), drmErrorVo.getDesc());
                throw new DRMException(strRetCode);
            }return Files.readAllBytes(Paths.get(dstPath.toFile().toURI())); // * 암호화 바이너리 반환
        } catch (IOException e) {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(DrmErrorEnum.FILE_IO.value());
            log.info("[FAIL][ENCRYPT][FILE IO] {} , {} , {} ", drmErrorVo.getCode(), drmErrorVo.getValue(), e.getMessage());
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        } finally {
            //04. 임시파일 삭제
            fileManagerService.clearTmpDir(srcPath);
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
            throw new DRMException(DrmErrorEnum.PLAIN_FILE_ON_DECRYPT_REQUEST.value());
        }

        //복호화 객체 생성
        Madec clMadec = new Madec("/MarkAnyServerInfo.dat"); //파일경로 수정 필요(프로퍼티)
        //long lFileLen = Files.size(srcPath);  //복호화 대상파일 크기
        String retVal;

        try (BufferedInputStream inFile = new BufferedInputStream(Files.newInputStream(srcPath));
             BufferedOutputStream outFile = new BufferedOutputStream(Files.newOutputStream(dstPath))) {
            long lFileLen = Files.size(srcPath);  //복호화 대상파일 크기
            long OutFileLength = clMadec.lGetDecryptFileSize(fileName, lFileLen, inFile); //🔴(파일명, lFileLen,inFile)

            if(OutFileLength<=0){
                retVal=clMadec.strGetErrorCode();
                //retVal=clMadec.strGetErrorCode();
                log.error("[DECRYPT][ErrorCode] : {} [ErrorMessage] : {} ", retVal, clMadec.strGetErrorMessage(retVal));
            }
            //03. 복호화 진행
            retVal=clMadec.strMadec(outFile);
            int strRetCode = Integer.parseInt(retVal);
            log.debug("[DECRYPT][복호화 결과] :{}", retVal);
            //정상이 아니면 에러발생
            if(strRetCode==0){
                DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(strRetCode);
                log.info("[FAIL][DECRYPT][MARKANY] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
                checkResult(strRetCode);
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

    private boolean checkResult(int retVal) throws DRMException {
        if(retVal==0){
            return true;
        }else {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(retVal);
            log.info("[FAIL][ENCRYPT][MARKANY] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
            throw new DRMException(retVal);
        }
    }
}
