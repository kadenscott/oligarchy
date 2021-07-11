package dev.kscott.oligarchy;

import com.mongodb.lang.NonNull;
import de.gesellix.docker.client.DockerClient;
import de.gesellix.docker.client.DockerClientImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OligarchyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OligarchyApplication.class, args);
    }

    @Bean
    public @NonNull CommandLineRunner runner(final @NonNull ApplicationContext ctx) {
        return args -> {
            DockerClient client = new DockerClientImpl();
            System.out.println(client.info().getContent().toString());
        };
    }

}
