package com.mnemon1k.dwitter;

import com.mnemon1k.dwitter.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaticResourcesTest {
    AppConfiguration appConfig;
    MockMvc mockMvc;

    @Autowired
    public StaticResourcesTest(AppConfiguration appConfig, MockMvc mockMvc) {
        this.appConfig = appConfig;
        this.mockMvc = mockMvc;
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExists(){
        File uploadFolder = new File(appConfig.getUploadPath());
        boolean uploadFolderExists = uploadFolder.exists() && uploadFolder.isDirectory();
        assertThat(uploadFolderExists).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_profileImageSubFolderMustExists(){
        String profileImagesFolderPath = appConfig.getFullProfileImagesPath();

        File profileImagesFolder = new File(profileImagesFolderPath);
        boolean profileImagesFolderExists = profileImagesFolder.exists() && profileImagesFolder.isDirectory();
        assertThat(profileImagesFolderExists).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_attachmentsSubFolderMustExists(){
        String attachmentsFolderPath = appConfig.getFullAttachmentsPath();

        File attachmentsFolder = new File(attachmentsFolderPath);
        boolean attachmentsFolderExists = attachmentsFolder.exists() && attachmentsFolder.isDirectory();
        assertThat(attachmentsFolderExists).isTrue();
    }

    @Test
    public void getStaticFile_whenImageExistsInProfileUploadFolder_receiveOk() throws Exception {
        String fileName = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();

        File target = new File(appConfig.getFullProfileImagesPath() + "/" + fileName);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/" + appConfig.getProfileImagesFolder() + "/" + fileName))
                .andExpect(status().isOk());
    }

    @Test
    public void getStaticFile_whenImageExistsInAttachmentFolder_receiveOk() throws Exception {
        String fileName = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();

        File target = new File(appConfig.getFullAttachmentsPath() + "/" + fileName);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/" + appConfig.getAttachmentsFolder() + "/" + fileName))
                .andExpect(status().isOk());
    }

    @Test
    public void getStaticFile_whenImageDoesNotExists_receiveNotFound() throws Exception {
        mockMvc.perform(get("/images/" + appConfig.getAttachmentsFolder() + "/undefined-file.png"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStaticFile_whenImageExistsInAttachmentFolder_receiveOkWithCacheHeaders() throws Exception {
        String fileName = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();

        File target = new File(appConfig.getFullAttachmentsPath() + "/" + fileName);
        FileUtils.copyFile(source, target);

        MvcResult mvcResult = mockMvc.perform(get("/images/" + appConfig.getAttachmentsFolder() + "/" + fileName))
                .andReturn();

        String cacheControl = Objects.requireNonNull(mvcResult.getResponse().getHeaderValue("Cache-Control")).toString();

        assertThat(cacheControl).containsIgnoringCase("max-age=31536000");
    }

    @AfterEach
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }
}
