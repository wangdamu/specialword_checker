package com.mumu.specialword.specialword.service.impl;

import com.mumu.specialword.specialword.service.DirFilesSpecialWordService;
import com.mumu.specialword.specialword.service.FileListService;
import com.mumu.specialword.specialword.service.SpecialWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

@Service
public class DirFilesSpecialWordServiceImpl implements DirFilesSpecialWordService{

    @Autowired
    private FileListService fileListService;

    @Autowired
    private SpecialWordService specialWordService;

    @Override
    public void findSpecialWordsOfDirFiles(File rootDir, File outFile) {
        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }

        StringBuilder specialWords = new StringBuilder();
        List<File> fileList = fileListService.findAllFiles(rootDir, file -> file.isDirectory() || file.getName().endsWith(".java"));
        fileList.forEach(t->{
            specialWordService.checkSpecialWords(t, specialWords);
        });

        try (PrintStream ps = new PrintStream(new FileOutputStream(outFile));){
            ps.println(specialWords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
