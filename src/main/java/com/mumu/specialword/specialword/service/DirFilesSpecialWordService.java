package com.mumu.specialword.specialword.service;

import java.io.File;

/**
 * 目录下的文件的特殊字符
 */
public interface DirFilesSpecialWordService {

    /**
     * 查找目录下所有文件的中文字符所在位置
     * @param rootDir
     */
    public void findSpecialWordsOfDirFiles(File rootDir, File outFile);
}
