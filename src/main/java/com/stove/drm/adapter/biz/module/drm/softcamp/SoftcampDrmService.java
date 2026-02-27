package com.stove.drm.adapter.biz.module.drm.softcamp;

import SCSL.SLBsUtil;
import SCSL.SLDsFile;
import com.stove.drm.adapter.biz.exception.DRMException;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.stove.drm.adapter.biz.module.vo.DrmErrorVo;
import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoftcampDrmService {

    private final DrmProp prop;
    private final ResourceLoader resourceLoader;

    /**
     * 소프트 캠프 프로퍼티 호출.
     * @return
     * @throws IOException
     */
    private String getPropPath(){
        Path path = Path.of(prop.getSoftcampProperties());
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
        SLDsFile sFile = new SLDsFile();
        sFile.SettingPathForProperty(getPropPath());
        /*
        1 : 지원되는 확장자
        0 : 지원되지 않는 확장자
         */
        int ret = sFile.DSIsSupportFile(fileName);
        if(ret==1){
            return true;
        }
        log.info("[대상아님][sFile.DSIsSupportFile] Result ::: {}",ret);
        return false;
        
    }

    public boolean isEncrypted(Path path) {
        SLBsUtil sUtil = new SLBsUtil();
        /*
        1 : 암호화 파일
        0 : 일반 파일
        -1 : Exception 발생 시
         */
        int encrypted = sUtil.isEncryptFile(path.toString());
        
        if (encrypted == 1)  {
            // 암호화 파일의 경우 return 1
            log.info("[sFile.isEncrypted][Encrypted][암호화파일] Result ::: {}",encrypted);
            return true;
        } else{
            log.info("[sFile.isEncrypted][NOT Encrypted][일반파일]  Result ::: {}",encrypted);
            // 일반 파일의 경우  return 0
            return false;
        }
    }
 
    public byte[] encrypt(Path originFile, String targetFileName) throws DRMException {

        String srcFile = originFile.toAbsolutePath().toString();    
        String dstFile = originFile.getParent().toAbsolutePath() + "/" + targetFileName;    

        log.info("[ENCRYPT] : [SRC][{}] ::: [TARGET][{}]",srcFile,dstFile);

        SLDsFile sFile = new SLDsFile();
        sFile.SettingPathForProperty(getPropPath());

        sFile.SLDsInitDAC();
        sFile.SLDsAddUserDAC(prop.getDefaultDomain(), prop.getDefaultAuthBits(), 0, 0, 0);
/*
0 : 성공
이외의 값 : 실패
 */
        int retVal = sFile.SLDsEncFileDACV2(prop.getKeyfilePath(),
                prop.getGroupId(),
                srcFile,
                dstFile,
                1);
        //정상이 아니면 에러발생.
        log.debug("[encrypt][암호화 결과] :" + retVal);
        if(retVal!=0){
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(retVal);
            log.info("[FAIL][ENCRYPT][SOFTCAMP] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
            throw new DRMException(retVal);
        }
        checkResult(retVal);
        try {
            return Files.readAllBytes(Paths.get(dstFile));  // * 암호화 바이너리 반환
        } catch (IOException e) {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(DrmErrorEnum.FILE_IO.value());
            log.info("[FAIL][ENCRYPT][FILE IO] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(),e.getMessage());
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        }
    }

    public byte[] decrypt(Path encFile, String targetFileName) throws DRMException {
        
        String srcFile = encFile.toAbsolutePath().toString(); //암호화 파일 
        String dstFile = encFile.getParent().toAbsolutePath() + "/" + targetFileName; //복호화 파일 

        SLDsFile sFile = new SLDsFile();

        sFile.SettingPathForProperty(getPropPath());

        int retVal = sFile.CreateDecryptFileDAC (getKeyPath(),
                prop.getDefaultDomain(),
                srcFile,
                dstFile);
        log.debug("[decrypt][복호화 결과] :" + retVal);
        //정상이 아니면 에러발생.
        if(retVal!=0){
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(retVal);
            log.info("[FAIL][ENCRYPT][SOFTCAMP] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
            throw new DRMException(retVal);
        }
        try {
            return Files.readAllBytes(Paths.get(dstFile));
        } catch (IOException e) {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(DrmErrorEnum.FILE_IO.value());
            log.info("[FAIL][ENCRYPT][FILE IO] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(),e.getMessage());
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        }
    }
    
    private boolean checkResult(int retVal) throws DRMException {
        if(retVal==0){
            return true;
        }else {
            DrmErrorVo drmErrorVo = DrmErrorEnum.getDrmErrorVo(retVal);
            log.info("[FAIL][ENCRYPT][SOFTCAMP] {} , {} , {} ",drmErrorVo.getCode(),drmErrorVo.getValue(), drmErrorVo.getDesc());
            throw new DRMException(retVal);
        }
    }
}
