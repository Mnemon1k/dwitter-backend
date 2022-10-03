package com.mnemon1k.dwitter;

import com.github.javafaker.Faker;
import com.mnemon1k.dwitter.Record.Record;
import com.mnemon1k.dwitter.Record.RecordService;
import com.mnemon1k.dwitter.User.User;
import com.mnemon1k.dwitter.User.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.stream.IntStream;

@SpringBootApplication
public class DwitterApplication {

    private final Faker faker = new Faker();

    public static void main(String[] args) {
        SpringApplication.run(DwitterApplication.class, args);
    }

    // Set default validation language
    @Bean
    public LocaleResolver localeResolver() {

        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    @Profile("dev") // Set that this method will not run in tests
    CommandLineRunner run(UserService userService, RecordService recordService){
        return (args) -> {
            IntStream.rangeClosed(1, 15)
                    .mapToObj(i ->{
                        User user = new User();

                        user.setUsername(faker.name().username());
                        user.setDisplayName(faker.name().fullName());
                        user.setPassword("Password1");
                        user.setImage("");
                        return user;
                    })
                    .forEach(userService::save);

            User userqwe = new User();
            userqwe.setUsername("username1");
            userqwe.setDisplayName("username1");
            userqwe.setPassword("Password1");
            userqwe.setImage("");
            userService.save(userqwe);

            Record record = new Record();
            record.setContent(faker.lorem().sentence());
            record.setUser(userqwe);
            recordService.save(record, userqwe);

        };
    }
}
