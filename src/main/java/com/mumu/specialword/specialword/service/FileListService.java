package com.mumu.specialword.specialword.service;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 列出所有文件的Service
 */
public interface FileListService {

    /**
     * 找到根目录下所有的文件
     * @param rootDir
     * @param fileFilter
     * @return
     */
    List<File> findAllFiles(File rootDir, FileFilter fileFilter);
}
