package dev.kscott.oligarchy;

import dev.kscott.oligarchy.server.ServerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OligarchyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OligarchyApplication.class, args);
    }

    @Bean
    public CommandLineRunner docker(final ApplicationContext ctx) {
        return args -> {
            final ServerService serverService = ctx.getBean(ServerService.class);
        };
    }

}
