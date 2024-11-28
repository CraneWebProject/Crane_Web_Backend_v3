package sch.crane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CraneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CraneApplication.class, args);
    }

}
