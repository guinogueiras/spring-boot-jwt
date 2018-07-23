package com.guilhermenogueira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoJwtApplication.class, args);
	}
	
	@GetMapping("/")
	public String home() {
		return "Home Page Authenticated";
	}
	
	@GetMapping("/home")
	public String homePublic() {
		return "Home Page Public";
	}
}
