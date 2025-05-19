package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"vn.iotstar"})
@EnableJpaRepositories("vn.iotstar.repository")
public class DoAnCuoiKyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnCuoiKyApplication.class, args);
	}
}