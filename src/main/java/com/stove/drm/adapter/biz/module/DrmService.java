package com.stove.drm.adapter.biz.module;

import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

@Service
@RequiredArgsConstructor
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
     * @param ext
     * @return
     */
    public boolean checkExt(String ext) {
        try {
            String drmExt = load().getProperty(prop.getFileExt());
            for (String s : drmExt.split(";")) {
                if (s.equals(ext.toLowerCase())) {
                    return true;
                }
            }
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public boolean isEncrypted(Path data) {
        return false;
    }

    public byte[] encrypt(Path originFile,String targetFileName) {
        String srcFile= originFile.toAbsolutePath().toString();
        String dstFile= originFile.getParent().toAbsolutePath()+"/"+targetFileName;
        return new byte[0];
    }


    public byte[]  decrypt(byte[] plain, String drmLabel) {
        return new byte[0];
    }
}
