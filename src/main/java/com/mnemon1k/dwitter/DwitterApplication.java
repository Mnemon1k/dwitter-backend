package com.mnemon1k.dwitter;

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
    @Profile("!test") // Set that this method will not run in tests
    CommandLineRunner run(UserService userService){
        return (args) -> {
            IntStream.rangeClosed(1, 15)
                    .mapToObj(i ->{
                        User user = new User();
                        user.setUsername("user-" + i);
                        user.setDisplayName("display-name-" + i);
                        user.setPassword("Password1");
                        user.setImage("https://vjoy.cc/wp-content/uploads/2020/03/bezymyannyjmsakk.jpg");
                        return user;
                    })
                    .forEach(userService::save);
        };
    }
}
