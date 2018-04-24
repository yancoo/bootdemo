package hh.bootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
@ServletComponentScan // scan @WebListener
public class BootdemoApplication {
	// Tutorial: Spring Security and Angular
	// https://spring.io/guides/tutorials/spring-security-and-angular-js

	// curl -i -X POST -H "Content-Type:application/json" -d "{ \"firstName\" :
	// \"Frodo\", \"lastName\" : \"Baggins\" }" http://localhost:8080/people

	public static void main(String[] args) {
		SpringApplication.run(BootdemoApplication.class, args);
	}
}
