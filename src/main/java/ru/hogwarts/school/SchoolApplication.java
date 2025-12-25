package ru.hogwarts.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

@SpringBootApplication
public class SchoolApplication {

	public static void main(String[] args) {

		try {
			System.setOut(new PrintStream(System.out, true, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.setProperty("file.encoding", "UTF-8");

		SpringApplication.run(SchoolApplication.class, args);
	}

}
