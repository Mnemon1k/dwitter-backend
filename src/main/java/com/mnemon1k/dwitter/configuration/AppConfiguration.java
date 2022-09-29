package com.mnemon1k.dwitter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dwitter")
public class AppConfiguration {
    String uploadPath;
    String profileImagesFolder = "profile";
    String attachmentsFolder = "attachments";

    public String getFullProfileImagesPath(){
        return this.uploadPath + "/" + this.profileImagesFolder;
    }
    public String getFullAttachmentsPath(){
        return this.uploadPath + "/" + this.attachmentsFolder;
    }
}
