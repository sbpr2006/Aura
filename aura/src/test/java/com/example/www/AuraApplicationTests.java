package com.example.www;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.beats.AuraApplication;

@SpringBootTest(classes = AuraApplication.class, properties = "spring.profiles.active=test")
class AuraApplicationTests {

	@Test
	void contextLoads() {
	}

}
