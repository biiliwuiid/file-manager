package org.elsa.filemanager.job;

import org.elsa.filemanager.common.config.Config;
import org.elsa.filemanager.core.entity.FileSystem;
import org.elsa.filemanager.core.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author valor
 * @date 2018-11-23 15:22
 */
@Service
public class CleanUp {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private Config config;

    /**
     * 定时清理一定时间内没有使用过的文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpFile() {
        Long service = System.currentTimeMillis() - this.config.getExpiredDay() * 24 * 60 * 60 * 1000;
        List<FileSystem> list = this.fileMapper.queryExpiredFile(service);
        if (null != list && list.size() > 0) {

            // 循环删除数据库记录与文件
            for (FileSystem file : list) {
                this.fileMapper.deleteById(file.getId());

                File f = new File(this.config.getFileDir() + file.getSavedFilename());
                if (f.exists()) {
                    if (!f.delete()) {
                        System.out.println("[delete file failed] --- " + "id[" + file.getId() + "]" + "name[" + file.getSavedFilename() + "]");
                    }
                }
            }
        }
    }
}
