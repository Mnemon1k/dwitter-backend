package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.File.FileService;
import com.mnemon1k.dwitter.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class FileServiceTest {
    static FileService fileService;
    static AppConfiguration appConfig;

    @BeforeAll
    public static void init(){
        appConfig = new AppConfiguration();
        appConfig.setUploadPath("uploads-test");

        fileService = new FileService(appConfig);

        new File(appConfig.getUploadPath());
        new File(appConfig.getFullProfileImagesPath());
        new File(appConfig.getFullAttachmentsPath());
    }

    @Test
    public void detectType_whenPngFileProvided_returnImagePng() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-png.png");
        byte[] byteArray = FileUtils.readFileToByteArray(resource.getFile());

        String fileType = fileService.detectType(byteArray);
        assertThat(fileType).isEqualToIgnoringCase("image/png");
    }

    @AfterAll
    public static void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }
}
