package com.govtechparking.GovTechBackend;

import com.govtechparking.GovTechBackend.config.CorsProperties;
import com.govtechparking.GovTechBackend.config.JwtProperties;
import com.govtechparking.GovTechBackend.config.OtpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, OtpProperties.class, CorsProperties.class})
public class GovTechBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GovTechBackendApplication.class, args);
	}

}
