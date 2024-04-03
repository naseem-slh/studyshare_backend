package com.team73.studyshare;

import com.team73.studyshare.util.MockDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * The main class for the Studyshare application.
 *
 * This class serves as the entry point for the application and contains the main method
 * that starts the Spring Boot application. It also defines a command line runner bean
 * for test purposes.
 */
@SpringBootApplication
public class StudyshareApplication {

	@Autowired
	private MockDataBuilder mockDataBuilder;

	public static void main(String[] args) {
		SpringApplication.run(StudyshareApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return runner -> mockDataBuilder.generateMockData();
	}
}


