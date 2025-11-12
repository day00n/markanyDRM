package com.stove.drm.adapter.biz.module;

import com.stove.drm.adapter.biz.util.UUIDGen;
import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileManagerService {
    private final DrmProp prop;


    /** 오늘 날짜 기반 경로 생성 */
    private Path makeTmpDir() {

        // 1) UID 생성
        String UID = UUIDGen.generate();
        // 2) application.yml 경로의 {yymmdd} 치환
        String doorayFilePath = prop.getDoorayFilePath().replace("{seq}", UID);
        // 3) 디렉토리 생성 (없으면 자동 생성)
        Path path = Path.of(doorayFilePath);
        try {
            Files.createDirectories(Path.of(doorayFilePath));
        } catch (IOException e) {
            return null;
        }
        return path;
    }

    /** 해당 날짜 경로 아래 파일 생성 */
    public Path createFile(String fileName, byte[] content) {
        Path dir = makeTmpDir();
        Path filePath = dir.resolve(fileName);
        try {
            Files.write(filePath, content);
        } catch (IOException e) {
            return null;
        }
        return filePath;
    }

    /**
     * 임시파일 클리어.
     */
    public void clearTmpDir(Path path){
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
