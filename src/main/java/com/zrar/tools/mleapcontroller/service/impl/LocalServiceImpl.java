package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.config.LocalConfig;
import com.zrar.tools.mleapcontroller.service.LocalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class LocalServiceImpl implements LocalService {

    @Autowired
    private LocalConfig localConfig;

    @Override
    public boolean saveToLocal(String filename, byte[] data) {
        try {
            // 如果待保存的文件夹不存在，那就创建一个文件夹
            File folder = new File(localConfig.getFolder());
            if (! folder.exists()) {
                folder.mkdirs();
            }
            // 然后把文件保存下来
            File file = new File(localConfig.getFolder(), filename);
            IOUtils.copy(new ByteArrayInputStream(data), new FileOutputStream(file));
            return true;
        } catch (IOException e) {
            log.error("e = {}", e);
            return false;
        }
    }

    @Override
    public String getLocalFilePath(String filename) {
        return localConfig.getFolder() + File.separator + filename;
    }

    @Override
    public void clearAll() {
        File folder = new File(localConfig.getFolder());
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}
