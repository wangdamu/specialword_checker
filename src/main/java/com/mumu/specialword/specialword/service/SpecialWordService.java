package com.mumu.specialword.specialword.service;

import java.io.File;

/**
 * 特殊字符Service
 */
public interface SpecialWordService {
    /**
     * 检测特殊字符
     * @param file
     * @param specialWords
     */
    public void checkSpecialWords(File file, StringBuilder specialWords);
}
