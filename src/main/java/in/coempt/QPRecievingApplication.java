package in.coempt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "in.coempt.entity")
@EnableJpaRepositories(basePackages = "in.coempt.repository")
public class QPRecievingApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(QPRecievingApplication.class, args);
	}

}
