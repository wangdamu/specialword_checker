package com.mumu.specialword.specialword.service.impl;

import com.mumu.specialword.specialword.service.FileListService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileListServiceImpl implements FileListService {

    @Override
    public List<File> findAllFiles(File rootDir, FileFilter fileFilter) {
        List<File> ret = new ArrayList<>();
        List<File> dirList = new LinkedList<>();
        dirList.add(rootDir);

        while (dirList.size() > 0) {
            File dir = dirList.remove(0);
            File[] files = dir.listFiles(fileFilter);
            for (File file : files) {
                if (file.isFile()) {
                    ret.add(file);
                } else {
                    dirList.add(file);
                }
            }
        }

        return ret;
    }
}
