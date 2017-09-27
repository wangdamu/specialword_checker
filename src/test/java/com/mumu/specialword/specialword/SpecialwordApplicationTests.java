package com.mumu.specialword.specialword;

import com.mumu.specialword.specialword.service.DirFilesSpecialWordService;
import com.mumu.specialword.specialword.service.SpecialWordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpecialwordApplicationTests {

	@Autowired
	private SpecialWordService specialWordService;

	@Autowired
	private DirFilesSpecialWordService dirFilesSpecialWordService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSpecialWords(){
		File file = new File("/media/peter/Data/workspace/pims/src/trunk/pims-backend/pims-rest/api-centre/src/main/java/cn/mulberrylearning/apicentre/service/impl/RfidSignServiceImpl.java");
		StringBuilder sb = new StringBuilder();
		specialWordService.checkSpecialWords(file, sb);
		System.out.println(sb);
	}

	@Test
	public void testDirFilesSpecialWords(){
		File file = new File("/media/peter/Data/workspace/pims/src/trunk/pims-backend/");
		File outFile = new File("/media/peter/Data/work/pims-specialwords/pims-specialwords.txt");
		dirFilesSpecialWordService.findSpecialWordsOfDirFiles(file, outFile);
	}
}
