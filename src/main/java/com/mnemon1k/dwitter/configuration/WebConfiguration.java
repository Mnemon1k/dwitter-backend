package com.mnemon1k.dwitter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    AppConfiguration appConfig;

    @Autowired
    public WebConfiguration(AppConfiguration appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + appConfig.getUploadPath() + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

    @Bean
    CommandLineRunner createUploadFolder(){
        return (args) -> {
            checkAndCreateFolder(appConfig.getUploadPath());
            checkAndCreateFolder(appConfig.getFullProfileImagesPath());
            checkAndCreateFolder(appConfig.getFullAttachmentsPath());
        };
    }

    private void checkAndCreateFolder(String path) {
        File folder = new File(path);
        boolean folderExists = folder.exists() && folder.isDirectory();
        if (!folderExists){
            folder.mkdir();
        }
    }
}
