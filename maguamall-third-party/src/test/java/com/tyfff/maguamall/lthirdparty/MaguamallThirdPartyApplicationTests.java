package com.tyfff.maguamall.lthirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class MaguamallThirdPartyApplicationTests {
	@Autowired
	OSSClient ossClient;

	@Test
	void contextLoads() {
	}

	@Test
	void upload() throws FileNotFoundException {
		FileInputStream stream = new FileInputStream("C:\\Users\\18042\\IdeaProjects\\maguamall\\maguamall-third-party\\pom.xml");
		ossClient.putObject("maguamall","test.pom",stream);
		ossClient.shutdown();

	}
}
