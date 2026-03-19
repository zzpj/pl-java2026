package com.example.OpenApiDemo.demo;

import com.zzpj.OpenApiDemo.api.HealthApi;
import com.zzpj.OpenApiDemo.model.HealthGet200Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

@RestController
class HelloController implements HealthApi {

	@Override
	public ResponseEntity<HealthGet200Response> healthGet() {
		return ResponseEntity.of(java.util.Optional.of(new HealthGet200Response().status(HealthGet200Response.StatusEnum.UP)));
	}
}