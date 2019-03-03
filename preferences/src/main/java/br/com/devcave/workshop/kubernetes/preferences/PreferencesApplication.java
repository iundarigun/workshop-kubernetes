package br.com.devcave.workshop.kubernetes.preferences;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PreferencesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreferencesApplication.class, args);
	}

}
