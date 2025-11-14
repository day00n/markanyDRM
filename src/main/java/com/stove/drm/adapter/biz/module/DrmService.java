package com.stove.drm.adapter.biz.module;

import SCSL.SLBsUtil;
import SCSL.SLDsFile;
import com.stove.drm.adapter.biz.exception.DRMException;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrmService {

    private final DrmProp prop;
    private final ResourceLoader resourceLoader;

    /**
     * 소프트 캠프 프로퍼티 호출.
     * @return
     * @throws IOException
     */
    public Properties load() throws IOException {
        Resource resource = resourceLoader.getResource(prop.getSoftcampProperties());
        try (InputStream in = resource.getInputStream()) {
            Properties p = new Properties();
            p.load(in);
            return p;
        }
    }

    /**
     * 암호화 대상 확장자 확인.
     * @param fileName
     * @return
     */
    public boolean checkExt(String fileName) {
        SLDsFile sFile = new SLDsFile();
        sFile.SettingPathForProperty(prop.getSoftcampProperties());
        int ret = sFile.DSIsSupportFile(fileName);
        if(ret==0){
            return true;
        }
        return false;
        
    }

    public boolean isEncrypted(Path path) {
        SLBsUtil sUtil = new SLBsUtil();
        int encrypted = sUtil.isEncryptFile(path.toString());
        
        if (encrypted == 1)   // 암호화 파일의 경우 return 1
            return true;
        else                // 일반 파일의 경우  return 0
            return false;
    }
 
    public byte[] encrypt(Path originFile, String targetFileName) throws DRMException {

        String srcFile = originFile.toAbsolutePath().toString();    
        String dstFile = originFile.getParent().toAbsolutePath() + "/" + targetFileName;    

        SLDsFile sFile = new SLDsFile();
        sFile.SettingPathForProperty(prop.getSoftcampProperties());

        sFile.SLDsInitDAC();
        sFile.SLDsAddUserDAC(prop.getDefaultDomain(), prop.getDefaultAuthBits(), 0, 0, 0);

        int retVal = sFile.SLDsEncFileDACV2(prop.getKeyfilePath(),
                prop.getGroupId(),
                srcFile,
                dstFile,
                1);
        log.debug("SLDsEncFileDAC :" + retVal);
        //정상이 아니면 에러발생.
        if(retVal!=0){
            throw new DRMException(retVal);
        }
        checkResult(retVal);
        try {
            return Files.readAllBytes(Paths.get(dstFile));  // * 암호화 바이너리 반환
        } catch (IOException e) {
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        }
    }

    public byte[] decrypt(Path encFile, String targetFileName) throws DRMException {
        
        String srcFile = encFile.toAbsolutePath().toString(); //암호화 파일 
        String dstFile = encFile.getParent().toAbsolutePath() + "/" + targetFileName; //복호화 파일 

        SLDsFile sFile = new SLDsFile();

        sFile.SettingPathForProperty(prop.getSoftcampProperties());

        int retVal = sFile.CreateDecryptFileDAC (prop.getKeyfilePath(),
                prop.getDefaultDomain(),
                srcFile,
                dstFile);
        log.debug("CreateDecryptFileDAC :" + retVal);
        //정상이 아니면 에러발생.
        if(retVal!=0){
            throw new DRMException(retVal);
        }
        try {
            return Files.readAllBytes(Paths.get(dstFile));
        } catch (IOException e) {
            throw new DRMException(DrmErrorEnum.FILE_IO.value());
        }
    }
    
    private boolean checkResult(int retVal) throws DRMException {
        if(retVal==0){
            return true;
        }else {
            throw new DRMException(retVal);
        }
    }
}
